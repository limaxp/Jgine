package org.jgine.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgine.utils.logger.Logger;

/**
 * Helper class for reflection.
 */
public class Reflection implements Iterable<Class<?>> {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	private final List<Class<?>> classList;

	public Reflection(String pkg) {
		if (pkg == null)
			classList = getClasses();
		else
			classList = getClasses(pkg);
	}

	public List<Class<?>> perType(Class<?> type) {
		List<Class<?>> result = new ArrayList<Class<?>>();
		forEach(type, result::add);
		return result;
	}

	public void forEach(Class<?> type, Consumer<? super Class<?>> func) {
		for (Class<?> clazz : classList)
			if (type.isAssignableFrom(clazz))
				func.accept(clazz);
	}

	public List<Class<?>> perName(String name) {
		List<Class<?>> result = new ArrayList<Class<?>>();
		forEach(name, result::add);
		return result;
	}

	public void forEach(String name, Consumer<? super Class<?>> func) {
		for (Class<?> clazz : classList)
			if (clazz.getSimpleName().contains(name))
				func.accept(clazz);
	}

	public List<Class<?>> startsWith(String name) {
		List<Class<?>> result = new ArrayList<Class<?>>();
		forEach(name, result::add);
		return result;
	}

	public void forEach_StartsWith(String name, Consumer<? super Class<?>> func) {
		for (Class<?> clazz : classList)
			if (clazz.getSimpleName().startsWith(name))
				func.accept(clazz);
	}

	public List<Class<?>> values() {
		return Collections.unmodifiableList(classList);
	}

	@Override
	public void forEach(Consumer<? super Class<?>> func) {
		classList.forEach(func);
	}

	@Override
	public Iterator<Class<?>> iterator() {
		return classList.iterator();
	}

	public static List<Class<?>> getClasses() {
		return getClassStream().collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesPerType(Class<?> type) {
		return getClassStream().filter((clazz) -> type.isAssignableFrom(clazz)).collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesPerName(String name) {
		return getClassStream().filter((clazz) -> clazz.getSimpleName().contains(name)).collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesStartsWithName(String name) {
		return getClassStream().filter((clazz) -> clazz.getSimpleName().startsWith(name)).collect(Collectors.toList());
	}

	public static Stream<Class<?>> getClassStream() {
		Stream<Class<?>> stream;
		Package[] pkgs = ClassLoader.getSystemClassLoader().getDefinedPackages();
		stream = getClassStream(pkgs[0].getName());
		for (int i = 1; i < pkgs.length; i++) {
			stream = Stream.concat(stream, getClassStream(pkgs[i].getName()));
		}
		return stream;
	}

	public static List<Class<?>> getClasses(String pkg) {
		return getClassStream(pkg).collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesPerType(String pkg, Class<?> type) {
		return getClassStream(pkg).filter((clazz) -> type.isAssignableFrom(clazz)).collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesPerName(String pkg, String name) {
		return getClassStream(pkg).filter((clazz) -> clazz.getSimpleName().contains(name)).collect(Collectors.toList());
	}

	public static List<Class<?>> getClassesStartsWithName(String pkg, String name) {
		return getClassStream(pkg).filter((clazz) -> clazz.getSimpleName().startsWith(name))
				.collect(Collectors.toList());
	}

	public static Stream<Class<?>> getClassStream(String pkg) {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(pkg.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines().filter(line -> line.endsWith(".class"))
				.map(line -> getClass(pkg, line.substring(0, line.lastIndexOf('.'))));
	}

	public static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + path + "'", e);
			return null;
		}
	}

	public static Class<?> getClass_(String path) throws ClassNotFoundException {
		return Class.forName(path);
	}

	public static Class<?> getClass(String pkg, String name) {
		try {
			return Class.forName(pkg + "." + name);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + pkg + "." + name + "'", e);
			return null;
		}
	}

	public static Class<?> getClass_(String pkg, String name) throws ClassNotFoundException {
		return Class.forName(pkg + "." + name);
	}

	public static Class<?> getClass(String pkg, String outer, String inner) {
		try {
			return Class.forName(pkg + '.' + outer + '$' + inner);
		} catch (ClassNotFoundException e) {
			Logger.err("Reflection: Error on reflecting class '" + pkg + '.' + outer + '$' + inner + "'", e);
			return null;
		}
	}

	public static Class<?> getClass_(String pkg, String outer, String inner) throws ClassNotFoundException {
		return Class.forName(pkg + '.' + outer + '$' + inner);
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			return getField_(clazz, name);
		} catch (NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting field '" + name + "' for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static Field getField_(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	public static Object getFieldValue(Object obj, String name) {
		try {
			return getFieldValue_(obj, name);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Logger.err("Reflection: Error on getting field value '" + name + "' for class '" + obj.getClass().getName()
					+ "'", e);
			return null;
		}
	}

	public static Object getFieldValue_(Object obj, String name)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getField_(obj.getClass(), name).get(obj);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return getMethod_(clazz, name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting method '" + name + "' for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static Method getMethod_(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getMethod(name, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return getDeclaredMethod_(clazz, name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting method '" + name + "' for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static Method getDeclaredMethod_(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getDeclaredMethod(name, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return getConstructor_(clazz, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> Constructor<T> getConstructor_(Class<T> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<T> method = clazz.getConstructor(parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return getDeclaredConstructor_(clazz, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static <T> Constructor<T> getDeclaredConstructor_(Class<T> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<T> method = clazz.getDeclaredConstructor(parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return getDeclaredConstructor_(clazz).newInstance();
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

	public static <T> T newInstance_(Constructor<T> constructor, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(args);
	}

	public static MethodHandle unreflectMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return unreflectMethod_(clazz, name, parameterTypes);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			Logger.err(
					"Reflection: Error on reflecting method handle '" + name + "' for class '" + clazz.getName() + "'",
					e);
			return null;
		}
	}

	public static MethodHandle unreflectMethod_(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws IllegalAccessException, NoSuchMethodException, SecurityException {
		return LOOKUP.unreflect(getMethod(clazz, name, parameterTypes));
	}

	public static MethodHandle unreflectMethod(Method method) {
		try {
			return unreflectMethod_(method);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on reflecting method handle '" + method.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectMethod_(Method method) throws IllegalAccessException {
		return LOOKUP.unreflect(method);
	}

	public static MethodHandle unreflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
		try {
			return unreflectConstructor_(clazz, parameterTypes);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on reflecting constructor handle for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectConstructor_(Class<?> clazz, Class<?>... parameterTypes)
			throws IllegalAccessException, NoSuchMethodException, SecurityException {
		return LOOKUP.unreflectConstructor(getDeclaredConstructor(clazz, parameterTypes));
	}

	public static MethodHandle unreflectConstructor(Constructor<?> constructor) {
		try {
			return unreflectConstructor_(constructor);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on reflecting constructor handle '" + constructor.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectConstructor_(Constructor<?> constructor) throws IllegalAccessException {
		return LOOKUP.unreflectConstructor(constructor);
	}

	public static MethodHandle unreflectGetter(Class<?> clazz, String name) {
		try {
			return unreflectGetter_(clazz, name);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectGetter_(Class<?> clazz, String name)
			throws IllegalAccessException, NoSuchFieldException, SecurityException {
		return LOOKUP.unreflectGetter(getField(clazz, name));
	}

	public static MethodHandle unreflectGetter(Field field) {
		try {
			return unreflectGetter_(field);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + field.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectGetter_(Field field) throws IllegalAccessException {
		return LOOKUP.unreflectGetter(field);
	}

	public static MethodHandle unreflectSetter(Class<?> clazz, String name) {
		try {
			return unreflectSetter_(clazz, name);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectSetter_(Class<?> clazz, String name)
			throws IllegalAccessException, NoSuchFieldException, SecurityException {
		return LOOKUP.unreflectSetter(getField(clazz, name));
	}

	public static MethodHandle unreflectSetter(Field field) {
		try {
			return unreflectSetter_(field);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + field.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle unreflectSetter_(Field field) throws IllegalAccessException {
		return LOOKUP.unreflectSetter(field);
	}

	public static MethodHandle findConstructor(Class<?> clazz, MethodType type) {
		try {
			return findConstructor_(clazz, type);
		} catch (IllegalAccessException | NoSuchMethodException e) {
			Logger.err("Reflection: Error on creating constructor handle for class '" + clazz.getName() + "'", e);
			return null;
		}
	}

	public static MethodHandle findConstructor_(Class<?> clazz, MethodType type)
			throws NoSuchMethodException, IllegalAccessException {
		return LOOKUP.findConstructor(clazz, type);
	}

	public static MethodHandle findGetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return findGetter_(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating getter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle findGetter_(Class<?> clazz, String name, Class<?> type)
			throws NoSuchFieldException, IllegalAccessException {
		return LOOKUP.findGetter(clazz, name, type);
	}

	public static MethodHandle findSetter(Class<?> clazz, String name, Class<?> type) {
		try {
			return findSetter_(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static MethodHandle findSetter_(Class<?> clazz, String name, Class<?> type)
			throws NoSuchFieldException, IllegalAccessException {
		return LOOKUP.findSetter(clazz, name, type);
	}

	public static VarHandle findVarHandle(Class<?> clazz, String name, Class<?> type) {
		try {
			return findVarHandle_(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static VarHandle findVarHandle_(Class<?> clazz, String name, Class<?> type)
			throws NoSuchFieldException, IllegalAccessException {
		return LOOKUP.findVarHandle(clazz, name, type);
	}

	public static VarHandle findVarHandlePrivate(Class<?> clazz, String name, Class<?> type) {
		try {
			return findVarHandlePrivate_(clazz, name, type);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			Logger.err("Reflection: Error on creating setter handle for field '" + name + "'", e);
			return null;
		}
	}

	public static VarHandle findVarHandlePrivate_(Class<?> clazz, String name, Class<?> type)
			throws NoSuchFieldException, IllegalAccessException {
		return MethodHandles.privateLookupIn(clazz, LOOKUP).findVarHandle(clazz, name, type);
	}

	public static VarHandle findArrayVarHandle(Class<?> arrayClass) {
		return MethodHandles.arrayElementVarHandle(arrayClass);
	}

	public static <T> T clone(T obj) {
		try {
			return clone_(obj);
		} catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.err("Reflection: Error on shallow clone!", e);
			return null;
		}
	}

	public static <T> T clone_(T obj) throws IllegalAccessException, InstantiationException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> clazz = obj.getClass();
		@SuppressWarnings("unchecked")
		T newEntity = (T) obj.getClass().getDeclaredConstructor().newInstance();

		while (clazz != null) {
			copyFields(obj, newEntity, clazz);
			clazz = clazz.getSuperclass();
		}
		return newEntity;
	}

	public static <T> T copyFields(T entity, T newEntity, Class<?> clazz) {
		try {
			return copyFields_(entity, newEntity, clazz);
		} catch (IllegalAccessException e) {
			Logger.err("Reflection: Error on field copy!", e);
			return null;
		}
	}

	public static <T> T copyFields_(T entity, T newEntity, Class<?> clazz) throws IllegalAccessException {
		List<Field> fields = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()))
				fields.add(field);
		}
		for (Field field : fields) {
			field.setAccessible(true);
			field.set(newEntity, field.get(entity));
		}
		return newEntity;
	}
}
