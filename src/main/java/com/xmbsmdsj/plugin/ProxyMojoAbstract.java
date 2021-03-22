package com.xmbsmdsj.plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.util.ClasspathHelper;

@Mojo(name = "proxy", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ProxyMojoAbstract extends AbstractReflexyMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		getLog().info(Arrays.toString(proxyInterfaces));
		verifyInterfaceExistences();
		Set<List<String>> proxyConfigs = generateProxyConfigurations();
		dump(proxyConfigs, proxyConfigFile);
	}

	private Set<List<String>> generateProxyConfigurations() {
		Set<List<String>> res = new HashSet<>();
		for (String interfaceNameLst : proxyInterfaces) {
			res.add(Arrays.asList(interfaceNameLst.split(",")));
		}
		return res;
	}

	/**
	 * This actually does nothing in proxy Mojo
	 * This method is just invoked to emit some warnings
	 */
	private void verifyInterfaceExistences() {
		ClassLoader projectClassLoader = getProjectClassLoader();
		for (String interfaceNameLst : proxyInterfaces) {
			for (String interfaceName : interfaceNameLst.split(",")) {
				try {
					ClasspathHelper.forClass(Class.forName(interfaceName), projectClassLoader);
				} catch (ClassNotFoundException e) {
					getLog().warn("Interface " + interfaceName + " not found. Skipping");
				}
			}
		}

	}

}
