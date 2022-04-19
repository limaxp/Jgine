package org.jgine.core.manager;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.jgine.misc.utils.memory.UnsafeHelper;
import org.lwjgl.system.MemoryUtil;

public class PlatformManager {

	public static String getOperatingSystem() {
		return System.getProperty("os.name");
	}

	public static String getVersion() {
		return System.getProperty("os.version");
	}

	public static String getArchitecture() {
		return System.getProperty("os.arch");
	}

	public static int getBitArchitecture() {
		return Integer.parseInt(System.getProperty("sun.arch.data.model"));
	}

	public static int getProcessorsSize() {
		return Runtime.getRuntime().availableProcessors();
	}

	@SuppressWarnings("restriction")
	public static long getPageSize() {
		return UnsafeHelper.UNSAFE.pageSize();
	}

	public static long getCacheLineSize() {
		return MemoryUtil.CACHE_LINE_SIZE;
	}

	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static Dimension getScreenResolution() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
}
