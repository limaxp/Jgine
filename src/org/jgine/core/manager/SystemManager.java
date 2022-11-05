package org.jgine.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jgine.misc.utils.logger.Logger;
import org.jgine.system.EngineSystem;

public class SystemManager {

	public static final int MAX_SIZE = 1000;

	private static final Map<String, EngineSystem> NAME_MAP = new HashMap<String, EngineSystem>(MAX_SIZE);
	private static final Map<Class<? extends EngineSystem>, EngineSystem> CLASS_MAP = new IdentityHashMap<Class<? extends EngineSystem>, EngineSystem>(
			MAX_SIZE);
	private static EngineSystem[] ID_MAP = new EngineSystem[MAX_SIZE];
	private static int size;

	public static <T extends EngineSystem> T register(String name, T system) {
		if (NAME_MAP.containsKey(name)) {
			Logger.warn("SystemManager: System '" + name + "' does already exist!");
			return system;
		}
		NAME_MAP.put(name, system);
		if (!CLASS_MAP.containsKey(system.getClass()))
			CLASS_MAP.put(system.getClass(), system);
		int id = size++;
		ID_MAP[id] = system;
		system.init(id, name);
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

	@SuppressWarnings("unchecked")
	public static <T extends EngineSystem> T get(int id) {
		return (T) ID_MAP[id];
	}

	public static Collection<EngineSystem> getSystems() {
		return NAME_MAP.values();
	}

	public static int getSystemSize() {
		return size;
	}
}
