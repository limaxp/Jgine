package org.jgine.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jgine.system.EngineSystem;
import org.jgine.utils.logger.Logger;

/**
 * Manager to register {@link EngineSystem}<code>s</code>. Created systems MUST
 * be registered here before using them!
 */
public class SystemManager {

	public static final int MAX_SIZE = 1000;

	private static final Map<String, EngineSystem> NAME_MAP = new HashMap<String, EngineSystem>(MAX_SIZE);
	private static EngineSystem[] ID_MAP = new EngineSystem[MAX_SIZE];
	private static int size;

	public static <T extends EngineSystem> T register(T system) {
		if (NAME_MAP.containsKey(system.name)) {
			Logger.warn("SystemManager: System '" + system.name + "' does already exist!");
			return system;
		}
		NAME_MAP.put(system.name, system);
		int id = size++;
		ID_MAP[id] = system;
		system.init(id);
		return system;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EngineSystem> T get(String name) {
		return (T) NAME_MAP.get(name);
	}

	@SuppressWarnings("unchecked")
	public static <T extends EngineSystem> T get(int id) {
		return (T) ID_MAP[id];
	}

	public static Collection<EngineSystem> getSystems() {
		return NAME_MAP.values();
	}

	public static int getSize() {
		return size;
	}
}
