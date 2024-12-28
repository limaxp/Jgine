package org.jgine.system.systems.script;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.Reflection;
import org.reflections.Reflections;

public interface Script {

	public static final Map<String, Supplier<Script>> MAP = new HashMap<String, Supplier<Script>>();

	public static void register(Package pkg) {
		String name = pkg.getName();
		Reflections reflections = new Reflections(name.substring(0, name.indexOf('.')));
		for (Class<?> c : reflections.getSubTypesOf(Script.class)) {
			MAP.put(c.getSimpleName(), () -> (Script) Reflection.newInstance(c));
		}
	}

	public default Object invokeFunction(String name, Class<?> type, Object arg) {
		try {
			return Reflection.getDeclaredMethod_(getClass(), name, type).invoke(this, arg);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public default Object invokeFunction(String name, Class<?> type1, Class<?> type2, Object arg1, Object arg2) {
		try {
			return Reflection.getDeclaredMethod_(getClass(), name, type1, type2).invoke(this, arg1, arg2);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public default Object invokeFunction(String name, Class<?>[] types, Object... args) {
		try {
			return Reflection.getDeclaredMethod_(getClass(), name, types).invoke(this, args);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Nullable
	public static Script get(String name) {
		return MAP.getOrDefault(name, () -> null).get();
	}
}
