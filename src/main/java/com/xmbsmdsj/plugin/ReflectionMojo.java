package com.xmbsmdsj.plugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@Mojo(name = "reflection")
public class ReflectionMojo extends AbstractMojo {

	@Parameter(name = "reflectClasses")
	String[] reflectClasses;

	@Parameter(name = "reflectPackages")
	String[] reflectPackages;

	@Parameter(name = "excludeClasses")
	String[] excludeClasses; 

	@Parameter(name = "reflectConfigFile", defaultValue = "xm-reflection-config.json")
	String reflectConfigFile;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (reflectClasses == null) {
			reflectClasses = new String[]{};
		}
		if (reflectPackages == null) {
			reflectPackages = new String[]{};
		}
		if (reflectConfigFile == null) {
			reflectConfigFile = "xm-reflection-config.json";
		}
		getLog().info("Reflection Mojo invoked");
		Reflections reflections = getReflectionsFromPackagesAndClasses();
		Set<ReflectionConfigEntry> entries = generateReflectionConfigs(reflections);
		dump(entries);
	}

	private void dump(Set<ReflectionConfigEntry> entries) {
		//Set<ReflectionConfigEntry> existing = load();
		File outFile = new File("src/main/resources", reflectConfigFile);

		try {
			if (!outFile.createNewFile()) {
				getLog().warn("Reflection config file exists, overwriting!");
			}
			FileOutputStream fos = new FileOutputStream(outFile);
			ObjectMapper om = new ObjectMapper();
			fos.write(om.writeValueAsBytes(entries));
			fos.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}


	private Set<ReflectionConfigEntry> generateReflectionConfigs(Reflections reflections) {
		Set<String> excludeSet = new HashSet<>();
		for (String s : this.excludeClasses) {
			excludeSet.add(s);
		}
		return reflections.getAllTypes().stream()
		.map(ReflectionConfigEntry::new)
		.filter(e -> !excludeSet.contains(e.name))
		.collect(Collectors.toSet());
	}

	private Reflections getReflectionsFromPackagesAndClasses() {
		HashSet<URL> urls = new HashSet<URL>();
		for (String pkg : reflectPackages) {
			urls.addAll(ClasspathHelper.forPackage(pkg));
		}

		for (String className : reflectClasses) {
			try {
				urls.add(ClasspathHelper.forClass(Class.forName(className)));
			} catch (ClassNotFoundException cnfe) {
				getLog().warn(String.format("Class %s not found. Skipping", className));
			}
		}

		return new Reflections(
				new ConfigurationBuilder()
						.setUrls(urls)
						.setScanners(new SubTypesScanner(false))
		);
	}

}
