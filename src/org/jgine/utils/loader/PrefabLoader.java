package org.jgine.utils.loader;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.entity.Prefab;
import org.jgine.core.entity.PrefabManager;
import org.jgine.system.EngineSystem;

/**
 * Helper class for loading {@link Prefab} files.
 */
public class PrefabLoader {

	private static final Map<String, Object> EMPTY_DATA = new HashMap<String, Object>();
	private static final Map<String, List<PrefabData>> STALLED_PARENTS_MAP = new HashMap<String, List<PrefabData>>();
	private static final Map<String, List<PrefabData>> STALLED_CHILDS_MAP = new HashMap<String, List<PrefabData>>();

	@Nullable
	public static Prefab load(String name, File file) {
		Map<String, Object> data = YamlLoader.load(file);
		if (data == null)
			return null;
		return load(name, data);
	}

	@Nullable
	public static Prefab load(String name, InputStream is) {
		Map<String, Object> data = YamlLoader.load(is);
		if (data == null)
			return null;
		return load(name, data);
	}

	public static Prefab load(String name, Map<String, Object> data) {
		Prefab prefab = new Prefab(name);
		loadParents(prefab, data);
		return prefab;
	}

	@SuppressWarnings("unchecked")
	private static Prefab loadParents(Prefab prefab, Map<String, Object> data) {
		Object parents = data.get("parents");
		if (parents instanceof List) {
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
		if (childs instanceof List) {
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
		Object systemData = data.get("systems");
		if (systemData instanceof Map) {
			for (Entry<String, Object> entry : ((Map<String, Object>) systemData).entrySet()) {
				String name = entry.getKey();
				EngineSystem system = EngineSystem.get(name);
				Object entryData = entry.getValue();
				if (entryData instanceof Map) {
					Map<String, Object> entryMap = (Map<String, Object>) entryData;
					if (system == null)
						system = EngineSystem.get(YamlHelper.toString(entryMap.get("system")));
					if (system != null)
						prefab.set(system, name, system.load(entryMap));
				} else if (system != null)
					prefab.set(system, name, system.load(EMPTY_DATA));
			}
		}

		Object prefabData = data.get("data");
		if (prefabData instanceof Map)
			prefab.setData((Map<String, Object>) prefabData);

		Object tagData = data.get("tags");
		if (tagData instanceof Number)
			prefab.setTagBits(((Number) tagData).longValue());
		if (tagData instanceof List) {
			for (String tagName : (List<String>) tagData)
				prefab.setTag(tagName);
		}

		Object transformData = data.get("transform");
		if (transformData instanceof Map)
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
