package org.jgine.system.systems.script;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.Reflection;
import org.jgine.utils.registry.ClassPathRegistry;

public interface ScriptBase {

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
	public static ScriptBase get(String name) {
		return ClassPathRegistry.getScript(name);
	}
}
