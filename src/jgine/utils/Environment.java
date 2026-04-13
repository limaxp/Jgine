package jgine.utils;

import jgine.utils.memory.MemoryHelper;

public class Environment {

	public static String operatingSystem() {
		return System.getProperty("os.name");
	}

	public static String version() {
		return System.getProperty("os.version");
	}

	public static String architecture() {
		return System.getProperty("os.arch");
	}

	public static int bitArchitecture() {
		return Integer.parseInt(System.getProperty("sun.arch.data.model"));
	}

	public static int availableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static long pageSize() {
		return MemoryHelper.getPageSize();
	}

	public static long cacheLineSize() {
		return MemoryHelper.cacheLineSize();
	}

	public static long freeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long totalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long maxMemory() {
		return Runtime.getRuntime().maxMemory();
	}
}
