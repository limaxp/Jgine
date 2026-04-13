package jgine.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for reflection.
 */
public class Reflection implements Iterable<Class<?>> {

	public static final byte REFERENCE_SIZE = 4;
	private static final Map<Class<?>, Integer> SIZE_MAP = new IdentityHashMap<Class<?>, Integer>();

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
		return reader.lines().filter(line -> line.endsWith(".class")).map(line -> {
			try {
				return getClass(pkg, line.substring(0, line.lastIndexOf('.')));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	public static Class<?> getClass(String path) throws ClassNotFoundException {
		return Class.forName(path);
	}

	public static Class<?> getClass(String pkg, String name) throws ClassNotFoundException {
		return Class.forName(pkg + "." + name);
	}

	public static Class<?> getClass(String pkg, String outer, String inner) throws ClassNotFoundException {
		return Class.forName(pkg + '.' + outer + '$' + inner);
	}

	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	public static Object getFieldValue(Object obj, String name)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getField(obj.getClass(), name).get(obj);
	}

	public static void setFieldValue(Object obj, String name, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		getField(obj.getClass(), name).set(obj, value);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getMethod(name, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Method method = clazz.getDeclaredMethod(name, parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<T> method = clazz.getConstructor(parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException {
		Constructor<T> method = clazz.getDeclaredConstructor(parameterTypes);
		method.setAccessible(true);
		return method;
	}

	public static <T> T newInstance_(Class<T> clazz) {
		try {
			return newInstance(clazz);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getDeclaredConstructor(clazz).newInstance();
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(args);
	}

	public static <T> T clone(T obj) throws IllegalAccessException, InstantiationException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> clazz = obj.getClass();
		@SuppressWarnings("unchecked")
		T newEntity = (T) obj.getClass().getDeclaredConstructor().newInstance();

		while (clazz != null) {
			cloneFields(obj, newEntity, clazz);
			clazz = clazz.getSuperclass();
		}
		return newEntity;
	}

	public static <T> T cloneFields(T source, T target, Class<?> clazz) throws IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				field.set(target, field.get(source));
			}
		}
		return target;
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

	public static int sizeOf(Iterable<Field> fields) {
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
		return REFERENCE_SIZE;
	}
}
