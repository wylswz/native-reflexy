package com.xmbsmdsj.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.reflections.util.ClasspathHelper;

public class MavenProjectClassLoaderFactory implements ClassLoaderFactory {

	public static ClassLoaderFactory newClassLoaderFactory(MavenProject project) {
		return new MavenProjectClassLoaderFactory(project);
	}

	private MavenProject project;
	private Log log;
	public MavenProjectClassLoaderFactory(MavenProject project) {
		this.project = project;
		this.log = new SystemStreamLog();
	}

	@Override
	public ClassLoader getClassLoader() {
		if (project == null) {
			log.warn("Project is null. Skip creating maven project class loader");
			return null;
		}
		List<URL> pathUrls = new ArrayList<>();
		try {
			for (String runtimePath: project.getRuntimeClasspathElements()) {
				File f = new File(runtimePath);
				pathUrls.add(f.toURI().toURL());
			}
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			log.error(e.getMessage());
		}
		log.info("Get " + pathUrls.size() + " URLs from project dependencies");

		return new URLClassLoader(pathUrls.toArray(new URL[0]), ClasspathHelper.contextClassLoader());

	}
}
