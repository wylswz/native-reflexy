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
import com.xmbsmdsj.classloaders.ClassLoaderFactory;
import com.xmbsmdsj.classloaders.MavenProjectClassLoaderFactory;
import com.xmbsmdsj.pojos.ReflectionConfigEntry;
import com.xmbsmdsj.utils.ConfigLocationUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@Mojo(name = "reflection", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ReflectionMojo extends ReflexyMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		getLog().info("Reflection Mojo invoked");
		Reflections reflections = getReflectionsFromPackagesAndClasses();
		if (reflections != null) {
			Set<ReflectionConfigEntry> entries = generateReflectionConfigs(reflections);
			dump(entries, reflectConfigFile);
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
