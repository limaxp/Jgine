package org.jgine.misc.utils.loader;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Prefab;
import org.jgine.core.entity.PrefabManager;
import org.jgine.core.manager.SystemManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.system.EngineSystem;

public class PrefabLoader {

	private static final Map<String, Object> EMPTY_DATA = new HashMap<String, Object>();
	private static final Map<String, List<PrefabData>> STALLED_PARENTS_MAP = new HashMap<String, List<PrefabData>>();
	private static final Map<String, List<PrefabData>> STALLED_CHILDS_MAP = new HashMap<String, List<PrefabData>>();

	@Nullable
	public static Prefab load(String name, File file) {
		Map<String, Object> data = YamlLoader.load(file);
		if (data == null)
			return null;
		return loadParents(new Prefab(name), data);
	}

	@Nullable
	public static Prefab load(String name, InputStream is) {
		Map<String, Object> data = YamlLoader.load(is);
		if (data == null)
			return null;
		return loadParents(new Prefab(name), data);
	}

	@SuppressWarnings("unchecked")
	private static Prefab loadParents(Prefab prefab, Map<String, Object> data) {
		Object parents = data.get("parents");
		if (parents != null && parents instanceof List) {
			for (String parentName : (List<String>) parents) {
				Prefab parent = Prefab.get(parentName);
				if (parent == null) {
					prefab.clear();
					addStalledParents(parentName, new PrefabData(prefab, data));
					return prefab;
				}
				prefab.addParent(parent);
			}
		}
		return loadChilds(prefab, data);
	}

	@SuppressWarnings("unchecked")
	private static Prefab loadChilds(Prefab prefab, Map<String, Object> data) {
		Object childs = data.get("childs");
		if (childs != null && childs instanceof List) {
			for (String childName : (List<String>) childs) {
				Prefab child = Prefab.get(childName);
				if (child == null) {
					prefab.clearChilds();
					addStalledChilds(childName, new PrefabData(prefab, data));
					return prefab;
				}
				prefab.addChild(child);
			}
		}
		return loadData(prefab, data);
	}

	@SuppressWarnings("unchecked")
	private static Prefab loadData(Prefab prefab, Map<String, Object> data) {
		Object systems = data.get("systems");
		if (systems != null && systems instanceof Map) {
			for (Entry<String, Object> entry : ((Map<String, Object>) systems).entrySet()) {
				EngineSystem system = SystemManager.get(entry.getKey());
				Object value = entry.getValue();
				if (value instanceof Map)
					prefab.set(system, system.load((Map<String, Object>) value));
				else
					prefab.set(system, system.load(EMPTY_DATA));
			}
		}

		Object prefabData = data.get("data");
		if (prefabData != null && prefabData instanceof Map)
			prefab.setData((Map<String, Object>) prefabData);

		Object transformData = data.get("transform");
		if (transformData != null && transformData instanceof Map)
			prefab.transform.load((Map<String, Object>) transformData);

		PrefabManager.register(prefab);
		loadStalledParents(prefab.name);
		loadStalledChilds(prefab.name);
		return prefab;
	}

	private static void addStalledParents(String name, PrefabData data) {
		List<PrefabData> stalledList = STALLED_PARENTS_MAP.get(name);
		if (stalledList == null)
			STALLED_PARENTS_MAP.put(name, stalledList = new UnorderedIdentityArrayList<PrefabData>());
		stalledList.add(data);
	}

	private static void loadStalledParents(String name) {
		List<PrefabData> stalled = STALLED_PARENTS_MAP.get(name);
		if (stalled != null) {
			STALLED_PARENTS_MAP.remove(name);
			for (PrefabData data : stalled)
				loadParents(data.prefab, data.data);
		}
	}

	private static void addStalledChilds(String name, PrefabData data) {
		List<PrefabData> stalledList = STALLED_CHILDS_MAP.get(name);
		if (stalledList == null)
			STALLED_CHILDS_MAP.put(name, stalledList = new UnorderedIdentityArrayList<PrefabData>());
		stalledList.add(data);
	}

	private static void loadStalledChilds(String name) {
		List<PrefabData> stalled = STALLED_CHILDS_MAP.get(name);
		if (stalled != null) {
			STALLED_CHILDS_MAP.remove(name);
			for (PrefabData data : stalled)
				loadChilds(data.prefab, data.data);
		}
	}

	private static final class PrefabData {

		private final Prefab prefab;
		private final Map<String, Object> data;

		private PrefabData(Prefab prefab, Map<String, Object> data) {
			this.prefab = prefab;
			this.data = data;
		}
	}
}
