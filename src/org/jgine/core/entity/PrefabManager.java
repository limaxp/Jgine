package org.jgine.core.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

public class PrefabManager {

	private static final Map<String, Prefab> NAME_MAP = new HashMap<String, Prefab>();
	private static final Map<Integer, Prefab> ID_MAP = new HashMap<Integer, Prefab>();

	public static void register(Prefab prefab) {
		NAME_MAP.put(prefab.name, prefab);
		ID_MAP.put(prefab.id, prefab);
	}

	public static void free(Prefab prefab) {
		NAME_MAP.remove(prefab.name);
		ID_MAP.remove(prefab.id);
	}

	@Nullable
	public static Prefab get(String name) {
		return NAME_MAP.get(name);
	}

	@Nullable
	public static Prefab get(int id) {
		return ID_MAP.get(id);
	}

	public static Collection<Prefab> values() {
		return NAME_MAP.values();
	}

	public static void clear() {
		NAME_MAP.clear();
		ID_MAP.clear();
	}
}
