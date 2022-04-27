package org.jgine.misc.utils.memory;

import java.lang.reflect.Field;

import org.jgine.misc.utils.logger.Logger;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryHelper {

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
}
