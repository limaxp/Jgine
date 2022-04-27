package org.jgine.misc.utils.memory;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryHelper {

	static final Unsafe UNSAFE = getUnsafeInstance();

	// TODO broken!
	public static long sizeOf(Object object) {
		return UNSAFE.getAddress(normalize(UNSAFE.getInt(object, 4L)) + 12L);
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
}
