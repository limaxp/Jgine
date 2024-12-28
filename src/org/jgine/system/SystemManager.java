package org.jgine.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jgine.utils.logger.Logger;

/**
 * Manager to register {@link EngineSystem}<code>s</code>. Created systems MUST
 * be registered here before using them!
 */
public class SystemManager {

	private static final Map<String, EngineSystem<?, ?>> NAME_MAP = new HashMap<String, EngineSystem<?, ?>>(1000);
	private static final EngineSystem<?, ?>[] ID_MAP = new EngineSystem[1000];
	private static int size;

	static int register(EngineSystem<?, ?> system) {
		if (NAME_MAP.containsKey(system.name)) {
			Logger.warn("SystemManager: System '" + system.name + "' does already exist!");
			return -1;
		}
		NAME_MAP.put(system.name, system);
		int id = size++;
		ID_MAP[id] = system;
		return id;
	}

	@SuppressWarnings("unchecked")
	static <T extends EngineSystem<?, ?>> T get(String name) {
		return (T) NAME_MAP.get(name);
	}

	@SuppressWarnings("unchecked")
	static <T extends EngineSystem<?, ?>> T get(int id) {
		return (T) ID_MAP[id];
	}

	static Collection<EngineSystem<?, ?>> values() {
		return NAME_MAP.values();
	}

	static int size() {
		return size;
	}
}
