package com.xmbsmdsj.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmbsmdsj.utils.ConfigLocationUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractReflexyMojo extends AbstractMojo implements ProjectAware {
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

	@Parameter(name = "proxyInterfaces")
	String[] proxyInterfaces;

	@Parameter(name = "proxyConfigFile", defaultValue = "xm-proxy-config.json")
	String proxyConfigFile;

	protected void init() {
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
			if (proxyConfigFile == null) {
				proxyConfigFile = "xm-proxy-config.json";
			}
			if (excludeClasses == null) {
				excludeClasses = new String[]{};
			}
			if (proxyInterfaces == null) {
				proxyInterfaces = new String[]{};
			}
		}
	}

	public void execute()throws MojoExecutionException, MojoFailureException {
		init();
	}

	protected void dump(Object entries, String configFile) {
		File outFile = new File(ConfigLocationUtils.getConfigLocation(configFile));

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

	@Override
	public MavenProject getProject() {
		return project;
	}
}
