package org.jgine.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jgine.misc.utils.logger.Logger;
import org.jgine.system.EngineSystem;

public class SystemManager {

	private static final Map<String, EngineSystem> NAME_MAP = new HashMap<String, EngineSystem>();
	private static final Map<Class<? extends EngineSystem>, EngineSystem> CLASS_MAP = new IdentityHashMap<Class<? extends EngineSystem>, EngineSystem>();

	public static <T extends EngineSystem> T register(String name, T system) {
		if (NAME_MAP.containsKey(name)) {
			Logger.warn("SystemManager: System '" + name + "' does already exist!");
			return system;
		}
		NAME_MAP.put(name, system);
		if (!CLASS_MAP.containsKey(system.getClass()))
			CLASS_MAP.put(system.getClass(), system);
		return system;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EngineSystem> T get(String name) {
		return (T) NAME_MAP.get(name);
	}

	@SuppressWarnings("unchecked")
	public static final <T extends EngineSystem> T get(Class<T> clazz) {
		return (T) CLASS_MAP.get(clazz);
	}

	public static final Collection<EngineSystem> getSystems() {
		return NAME_MAP.values();
	}
}
