package org.jgine.misc.utils.memory;

import org.lwjgl.system.MemoryUtil;

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

	public static void putByte(long adress, byte value) {
		UNSAFE.putByte(adress, value);
	}

	public static void putChar(long adress, char value) {
		UNSAFE.putChar(adress, value);
	}

	public static void putShort(long adress, short value) {
		UNSAFE.putShort(adress, value);
	}

	public static void putInt(long adress, int value) {
		UNSAFE.putInt(adress, value);
	}

	public static void putLong(long adress, long value) {
		UNSAFE.putLong(adress, value);
	}

	public static void putFloat(long adress, float value) {
		UNSAFE.putFloat(adress, value);
	}

	public static void putDouble(long adress, double value) {
		UNSAFE.putDouble(adress, value);
	}

	public static int getPageSize() {
		return UNSAFE.pageSize();
	}
}
