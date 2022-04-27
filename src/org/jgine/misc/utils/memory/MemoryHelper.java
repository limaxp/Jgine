package org.jgine.misc.utils.memory;

import java.lang.reflect.Field;

import org.jgine.misc.utils.logger.Logger;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryHelper {

	static final Unsafe UNSAFE;

	static {
		try {
			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			UNSAFE = (Unsafe) field.get(null);
		} catch (Exception e) {
			Logger.err("UnsafeHelper: Error an unsafe field reflection! Shutting down runtime!", e);
			throw new RuntimeException(e);
		}
	}

	public static int sizeOf(Object object) {
		return (int) UNSAFE.getAddress(normalize(UNSAFE.getInt(object, 4L)) + 12L);
	}

	public static long normalize(int value) {
		if (value >= 0)
			return value;
		return (~0L >>> 32) & value;
	}

	public static long allocateMemory(long bytes) {
		return UNSAFE.allocateMemory(bytes);
	}

	public static long reallocateMemory(long address, long bytes) {
		return UNSAFE.reallocateMemory(address, bytes);
	}

	public static void freeMemory(long adress) {
		UNSAFE.freeMemory(adress);
	}

	public static void copyMemory(Object object, long address, long bytes) {
		UNSAFE.copyMemory(object, 0, null, address, bytes);
	}

	public static void copyMemory(Object src, Object dest, long bytes) {
		UNSAFE.copyMemory(src, 0, dest, 0, bytes);
	}

	public static void copyMemory(long scrAddress, long destAddress, long bytes) {
		UNSAFE.copyMemory(null, scrAddress, null, destAddress, bytes);
	}

	public static void copyMemory(Object src, long scrOffset, Object dest, long destOffset, long bytes) {
		UNSAFE.copyMemory(src, scrOffset, dest, destOffset, bytes);
	}

	public static int getPageSize() {
		return UNSAFE.pageSize();
	}
}
