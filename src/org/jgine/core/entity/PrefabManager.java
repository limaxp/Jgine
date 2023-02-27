package org.jgine.core.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;

/**
 * Manager to register and free prefabs. Allows registration of prefabs not
 * loaded from file.
 */
public class PrefabManager {

	private static final List<Prefab> LIST = new UnorderedIdentityArrayList<Prefab>();
	private static final List<Prefab> LIST_VIEW = Collections.unmodifiableList(LIST);
	private static final Map<String, Prefab> NAME_MAP = new HashMap<String, Prefab>();
	private static final Map<Integer, Prefab> ID_MAP = new HashMap<Integer, Prefab>();

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
		return LIST_VIEW;
	}

	public static void clear() {
		LIST.clear();
		NAME_MAP.clear();
		ID_MAP.clear();
	}
}
