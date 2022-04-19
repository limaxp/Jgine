package org.jgine.misc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SizeOf {

	public static final byte REFERENCE_SIZE = 4;

	private static final Map<Class<?>, Integer> SIZE_MAP = new IdentityHashMap<Class<?>, Integer>();

	public static int sizeOf(Object o) {
		return sizeOf(o.getClass());
	}

	public static int sizeOf(Class<?> c) {
		Integer savedSize = SIZE_MAP.get(c);
		if (savedSize != null)
			return savedSize;

		int size = 0;
		Class<?> superClass = c.getSuperclass();
		while (superClass != null) {
			size += fieldSize(superClass);
			superClass = superClass.getSuperclass();
		}
		size += fieldSize(c);
		SIZE_MAP.put(c, size);
		return size;
	}

	private static int fieldSize(Class<?> c) {
		int size = 0;
		for (Field field : c.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()))
				size += sizeOf(field);
		}
		return size;
	}

	public static int sizeOf(Field... fields) {
		return sizeOf(Arrays.asList(fields));
	}

	public static int sizeOf(List<Field> fields) {
		int size = 0;
		for (Field field : fields)
			size += sizeOf(field);
		return size;
	}

	public static int sizeOf(Field field) {
		Class<?> type = field.getType();
		if (type == char.class)
			return Character.BYTES;
		else if (type == int.class)
			return Integer.BYTES;
		else if (type == float.class)
			return Float.BYTES;
		else if (type == double.class)
			return Double.BYTES;
		else if (type == byte.class)
			return Byte.BYTES;
		else if (type == boolean.class)
			return Byte.BYTES;
		else if (type == short.class)
			return Short.BYTES;
		else if (type == long.class)
			return Long.BYTES;
		else if (type instanceof Object)
			return REFERENCE_SIZE;
		return 0;
	}
}
