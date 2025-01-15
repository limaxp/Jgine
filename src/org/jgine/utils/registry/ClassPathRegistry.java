package org.jgine.utils.registry;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.script.Script;
import org.jgine.utils.Reflection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ClassPathRegistry {

	private static final Map<String, Supplier<Script>> SCRIPT_MAP = new HashMap<String, Supplier<Script>>();
	private static final Map<String, Supplier<InputHandler>> INPUT_MAP = new HashMap<String, Supplier<InputHandler>>();

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
			SCRIPT_MAP.put(c.getSimpleName(), () -> (Script) Reflection.newInstance(c));

		for (Class<?> c : reflections.getSubTypesOf(InputHandler.class))
			INPUT_MAP.put(c.getSimpleName(), () -> (InputHandler) Reflection.newInstance(c));
	}

	@Nullable
	public static Script getScript(String name) {
		return SCRIPT_MAP.getOrDefault(name, () -> null).get();
	}

	@Nullable
	public static InputHandler getInput(String name) {
		return INPUT_MAP.getOrDefault(name, () -> null).get();
	}
}
