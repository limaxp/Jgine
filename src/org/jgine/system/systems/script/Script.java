package org.jgine.system.systems.script;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.Reflection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public interface Script {

	public static final Map<String, Supplier<Script>> MAP = new HashMap<String, Supplier<Script>>();

	public static void init() {
		Collection<URL> allPackagePrefixes = Arrays.stream(Package.getPackages()).map(p -> p.getName())
				.map(s -> s.split("\\.")[0]).distinct().map(s -> ClasspathHelper.forPackage(s)).reduce((c1, c2) -> {
					Collection<URL> c3 = new HashSet<>();
					c3.addAll(c1);
					c3.addAll(c2);
					return c3;
				}).get();
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().addUrls(allPackagePrefixes).addScanners(Scanners.SubTypes));

		for (Class<?> c : reflections.getSubTypesOf(Script.class))
			MAP.put(c.getSimpleName(), () -> (Script) Reflection.newInstance(c));
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
