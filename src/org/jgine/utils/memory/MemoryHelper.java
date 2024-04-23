package org.jgine.utils.memory;

public class MemoryHelper {

	static final sun.misc.Unsafe UNSAFE = getUnsafeInstance();

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

	public static long allocate(long bytes) {
		return UNSAFE.allocateMemory(bytes);
	}

	public static long reallocate(long address, long bytes) {
		return UNSAFE.reallocateMemory(address, bytes);
	}

	public static void free(long adress) {
		UNSAFE.freeMemory(adress);
	}

	public static void copy(long scrAddress, long destAddress, long bytes) {
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

	public int addressSize() {
		return UNSAFE.addressSize();
	}

	public static int getPageSize() {
		return UNSAFE.pageSize();
	}

	/**
	 * Allocates an instance but does not run any constructor. Initializes the class
	 * if it has not yet been.
	 */
	public Object allocateInstance(Class<?> clazz) throws InstantiationException {
		return UNSAFE.allocateInstance(clazz);
	}

	/** Throws the exception without telling the verifier. */
	public void throwException(Throwable ee) {
		UNSAFE.throwException(ee);
	}
}
