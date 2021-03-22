package com.xmbsmdsj.utils;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class ConfigLocationUtils {
	public static String RESOURCE_ROOT = Path.of("src", "main", "resources").toString();
	public static String getConfigLocation(String inputPath) {
		if (inputPath.contains(File.separator)) {
			return inputPath;
		}
		return Path.of(RESOURCE_ROOT, inputPath).toString();
	}

}
