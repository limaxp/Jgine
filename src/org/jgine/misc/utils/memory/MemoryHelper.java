package org.jgine.misc.utils.memory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryHelper {

	static final Unsafe UNSAFE = getUnsafeInstance();

	private static sun.misc.Unsafe getUnsafeInstance() {
		java.lang.reflect.Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();
		for (java.lang.reflect.Field field : fields) {
			if (!field.getType().equals(sun.misc.Unsafe.class))
				continue;

			int modifiers = field.getModifiers();
			if (!(java.lang.reflect.Modifier.isStatic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers)))
				continue;

			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}
		throw new UnsupportedOperationException("Jgine requires sun.misc.Unsafe to be available.");
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

	public static void copyMemory(long scrAddress, long destAddress, long bytes) {
		UNSAFE.copyMemory(scrAddress, destAddress, bytes);
	}

	public static void copyArray(Object src, long destAddress, long bytes) {
		UNSAFE.copyMemory(src, 0, null, destAddress, bytes);
	}

	public static void copyArray(long scrAddress, Object dest, long bytes) {
		UNSAFE.copyMemory(null, scrAddress, dest, 0, bytes);
	}

	public static void copyArray(Object src, Object dest, long bytes) {
		UNSAFE.copyMemory(src, 0, dest, 0, bytes);
	}

	public static void copyArray(Object src, long scrOffset, Object dest, long destOffset, long bytes) {
		UNSAFE.copyMemory(src, scrOffset, dest, destOffset, bytes);
	}

	public static void putByte(long address, byte value) {
		UNSAFE.putByte(address, value);
	}

	public static void putChar(long address, char value) {
		UNSAFE.putChar(address, value);
	}

	public static void putShort(long address, short value) {
		UNSAFE.putShort(address, value);
	}

	public static void putInt(long address, int value) {
		UNSAFE.putInt(address, value);
	}

	public static void putLong(long address, long value) {
		UNSAFE.putLong(address, value);
	}

	public static void putFloat(long address, float value) {
		UNSAFE.putFloat(address, value);
	}

	public static void putDouble(long address, double value) {
		UNSAFE.putDouble(address, value);
	}

	public static byte getByte(long address) {
		return UNSAFE.getByte(address);
	}

	public static char getChar(long address) {
		return UNSAFE.getChar(address);
	}

	public static short getShort(long address) {
		return UNSAFE.getShort(address);
	}

	public static int getInt(long address) {
		return UNSAFE.getInt(address);
	}

	public static long getLong(long address) {
		return UNSAFE.getLong(address);
	}

	public static float getFloat(long address) {
		return UNSAFE.getFloat(address);
	}

	public static double getDouble(long address) {
		return UNSAFE.getDouble(address);
	}

	public static int getPageSize() {
		return UNSAFE.pageSize();
	}
}
