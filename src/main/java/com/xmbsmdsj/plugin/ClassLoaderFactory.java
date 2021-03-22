package com.xmbsmdsj.plugin;

public interface ClassLoaderFactory {
	/**
	 * Get classloader
	 * @return null if class loader is unfeasible
	 */
	ClassLoader getClassLoader();
}
