package org.jgine.misc.utils.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jgine.misc.utils.logger.Logger;

public class Reflection {

	public static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + path + "'", e);
			return null;
		}
	}

	public static Class<?> getClass(String pkg, String name) {
		try {
			return Class.forName(pkg + "." + name);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + pkg + "." + name + "'", e);
			return null;
		}
	}

	public static Class<?> getClass(String pkg, String outer, String inner) {
		try {
			return Class.forName(pkg + '.' + outer + '$' + inner);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + pkg + '.' + outer + '$' + inner + "'", e);
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			return ReflectionUtils.getField(clazz, name);
		} catch (NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting field '" + name + "' for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return ReflectionUtils.getMethod(clazz, name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting method '" + name + "' for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return ReflectionUtils.getConstructor(clazz, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return ReflectionUtils.getDeclaredConstructor(clazz, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return ReflectionUtils.getDeclaredConstructor(clazz).newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on creating new instance for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... args) {
		try {
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Logger.err("Reflection: Error on creating new instance for constructor '" + constructor.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return ReflectionUtils.unreflectMethod(clazz, name, parameterTypes);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			Logger.err(
					"Reflection: Error on reflecting method handle '" + name + "' for class '" + clazz.getName() + "'",
					e);
			return null;
		}
	}

	public static MethodHandle unreflectMethod(Method method) {
		try {
			return ReflectionUtils.unreflectMethod(method);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on reflecting method handle '" + method.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
		try {
			return ReflectionUtils.unreflectConstructor(clazz, parameterTypes);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor handle for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectConstructor(Constructor<?> constructor) {
		try {
			return ReflectionUtils.unreflectConstructor(constructor);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on reflecting constructor handle '" + constructor.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectGetter(Class<?> clazz, String name) {
		try {
			return ReflectionUtils.unreflectGetter(clazz, name);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectGetter(Field field) {
		try {
			return ReflectionUtils.unreflectGetter(field);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + field.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectSetter(Class<?> clazz, String name) {
		try {
			return ReflectionUtils.unreflectSetter(clazz, name);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectSetter(Field field) {
		try {
			return ReflectionUtils.unreflectSetter(field);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + field.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle findConstructor(Class<?> clazz, MethodType type) {
		try {
			return ReflectionUtils.findConstructor(clazz, type);
		} catch (IllegalAccessException | NoSuchMethodException e) {
			Logger.err("Reflection: Error on creating constructor handle for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle findGetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return ReflectionUtils.findGetter(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle findSetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return ReflectionUtils.findSetter(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static <T> T clone(T obj) {
		try {
			return ReflectionUtils.clone_(obj);
		} catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on shallow clone!", e);
			return null;
		}
	}

	public static <T> T copyFields(T entity, T newEntity, Class<?> clazz) {
		try {
			return ReflectionUtils.copyFields(entity, newEntity, clazz);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on field copy!", e);
			return null;
		}
	}
}
