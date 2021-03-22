package com.xmbsmdsj.plugin;

import com.xmbsmdsj.classloaders.ClassLoaderFactory;
import com.xmbsmdsj.classloaders.MavenProjectClassLoaderFactory;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.maven.project.MavenProject;
import org.reflections.util.ClasspathHelper;

public interface ProjectAware {
	MavenProject getProject();
	default ClassLoader getProjectClassLoader() {
		ClassLoaderFactory factory = MavenProjectClassLoaderFactory.newClassLoaderFactory(getProject());
		ClassLoader projectClassLoader = factory.getClassLoader();
		if (projectClassLoader == null) {
			return ClasspathHelper.contextClassLoader();
		} else {
			return projectClassLoader;
		}
	}
}
