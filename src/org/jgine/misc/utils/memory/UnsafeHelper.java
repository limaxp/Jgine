package org.jgine.misc.utils.memory;

import java.lang.reflect.Field;

import org.jgine.misc.utils.logger.Logger;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeHelper {

	public static final Unsafe UNSAFE;

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

	// This need some work. Does only work for 32 bit system!
	/**
	 * DIRTY HACK! Makes casting from the first to the second class possible.
	 * Apparently this can be used for some sort of multiple inheritance.
	 * 
	 * @param clazz1
	 * @param clazz2
	 */
	public static void makeCastPossible(Class<?> clazz1, Class<?> clazz2) {
		try {
			long firstclassAddress = UNSAFE.getInt(clazz1.newInstance(), 4L);
			long secondclassAddress = UNSAFE.getInt(clazz2.newInstance(), 4L);
			UNSAFE.putAddress(firstclassAddress + 36, secondclassAddress);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
