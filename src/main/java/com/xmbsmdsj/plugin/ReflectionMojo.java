package com.xmbsmdsj.plugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@Mojo(name = "reflection", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ReflectionMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(name = "reflectClasses")
	String[] reflectClasses;

	@Parameter(name = "reflectPackages")
	String[] reflectPackages;

	@Parameter(name = "excludeClasses")
	String[] excludeClasses; 

	@Parameter(name = "reflectConfigFile", defaultValue = "xm-reflection-config.json")
	String reflectConfigFile;

	private void init() {
		{
			if (reflectClasses == null) {
				reflectClasses = new String[]{};
			}
			if (reflectPackages == null) {
				reflectPackages = new String[]{};
			}
			if (reflectConfigFile == null) {
				reflectConfigFile = "xm-reflection-config.json";
			}
			if (excludeClasses == null) {
				excludeClasses = new String[]{};
			}
		}
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		init();
		getLog().info("Reflection Mojo invoked");
		Reflections reflections = getReflectionsFromPackagesAndClasses();
		if (reflections != null) {
			Set<ReflectionConfigEntry> entries = generateReflectionConfigs(reflections);
			dump(entries);
		}
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
		Set<String> excludeSet = new HashSet<>(Arrays.asList(this.excludeClasses));
		return reflections.getAllTypes().stream()
		.map(ReflectionConfigEntry::new)
		.filter(e -> !excludeSet.contains(e.name))
				.peek(e -> {getLog().info("Processing: " + e.name);})
		.collect(Collectors.toSet());
	}

	/**
	 * Get reflections object from declared packages and classes
	 * Return null if there's no class need to be processed
	 * @return
	 */
	private Reflections getReflectionsFromPackagesAndClasses() {
		HashSet<URL> urls = new HashSet<URL>();
		ClassLoaderFactory factory = MavenProjectClassLoaderFactory.newClassLoaderFactory(project);
		ClassLoader projectClassLoader = factory.getClassLoader();
		if (projectClassLoader == null) {
			projectClassLoader = ReflectionMojo.class.getClassLoader();
		}

		for (String pkg : reflectPackages) {
			Collection<URL> urlsInPkg = ClasspathHelper.forPackage(pkg, projectClassLoader);
			if (urlsInPkg != null && urlsInPkg.size() > 0) {
				urls.addAll(urlsInPkg);
			}

		}

		for (String className : reflectClasses) {
			try {
				urls.add(ClasspathHelper.forClass(Class.forName(className), projectClassLoader));
			} catch (ClassNotFoundException cnfe) {
				getLog().warn(String.format("Class %s not found. Skipping", className));
			}
		}

		getLog().info("" + urls.size() + " URLs have been scanned");
		if (urls.size() == 0) {
			return null;
		}

		return new Reflections(
				new ConfigurationBuilder()
						.setUrls(urls)
						.addClassLoaders(ClasspathHelper.staticClassLoader(),projectClassLoader)
						.setScanners(new SubTypesScanner(false))
		);
	}

}
