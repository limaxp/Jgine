package org.jgine.core.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.collection.list.UnorderedIdentityArrayList;

/**
 * Manager to register and free {@link Prefab}<code>s</code>. Allows
 * registration of {@link Prefab}<code>s</code> not loaded from file.
 */
public class PrefabManager {

	private static final List<Prefab> LIST = new UnorderedIdentityArrayList<Prefab>();
	private static final Map<String, Prefab> NAME_MAP = new HashMap<String, Prefab>(10000);
	private static final Map<Integer, Prefab> ID_MAP = new HashMap<Integer, Prefab>(10000);

	public static void register(Prefab prefab) {
		LIST.add(prefab);
		NAME_MAP.put(prefab.name, prefab);
		ID_MAP.put(prefab.id, prefab);
	}

	public static void free(Prefab prefab) {
		LIST.remove(prefab);
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

	@Nullable
	public static Prefab getPerIndex(int index) {
		return LIST.get(index);
	}

	public static List<Prefab> values() {
		return Collections.unmodifiableList(LIST);
	}

	public static void clear() {
		LIST.clear();
		NAME_MAP.clear();
		ID_MAP.clear();
	}
}
