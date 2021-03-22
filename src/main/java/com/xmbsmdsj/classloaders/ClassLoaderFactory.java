package com.xmbsmdsj.classloaders;

public interface ClassLoaderFactory {
	/**
	 * Get classloader
	 * @return null if class loader is unfeasible
	 */
	ClassLoader getClassLoader();
}
