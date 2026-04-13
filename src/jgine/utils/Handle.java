package jgine.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class for creating {@link MethodHandle} and {@link VarHandle}
 * instances.
 */
public class Handle {

	public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static Class<?> getClass(String name) {
		try {
			return LOOKUP.findClass(name);
		} catch (ClassNotFoundException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle special(Method method, Class<?> specialCaller) {
		try {
			return LOOKUP.unreflectSpecial(method, specialCaller);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle special(Class<?> clazz, String name, MethodType types, Class<?> specialCaller) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findSpecial(clazz, name, types, specialCaller);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle method(Method method) {
		try {
			return LOOKUP.unreflect(method);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle method(Class<?> clazz, String name, MethodType types) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findVirtual(clazz, name, types);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle staticMethod(Class<?> clazz, String name, MethodType types) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findStatic(clazz, name, types);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle constructor(Constructor<?> constructor) {
		try {
			return LOOKUP.unreflectConstructor(constructor);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle constructor(Class<?> clazz, MethodType type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findConstructor(clazz, type);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle getter(Field field) {
		try {
			return LOOKUP.unreflectGetter(field);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle getter(VarHandle handle) {
		return handle.toMethodHandle(VarHandle.AccessMode.GET);
	}

	public static MethodHandle getter(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findGetter(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle staticGetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findStaticGetter(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle setter(Field field) {
		try {
			return LOOKUP.unreflectSetter(field);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle setter(VarHandle handle) {
		return handle.toMethodHandle(VarHandle.AccessMode.SET);
	}

	public static MethodHandle setter(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findSetter(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static MethodHandle staticSetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findStaticSetter(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static VarHandle var(Field field) {
		try {
			return LOOKUP.unreflectVarHandle(field);
		} catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static VarHandle var(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findVarHandle(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static VarHandle staticVar(Class<?> clazz, String name, Class<?> type) {
		try {
			return MethodHandles.privateLookupIn(clazz, LOOKUP).findStaticVarHandle(clazz, name, type);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static VarHandle array(Class<?> arrayClass) {
		return MethodHandles.arrayElementVarHandle(arrayClass);
	}
}
