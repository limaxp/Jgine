package jgine.utils.loader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Prefab;
import jgine.core.Registry;
import jgine.system.EngineSystem;
import jgine.system.SystemObject;
import jgine.utils.ObjectUtils;
import jgine.utils.collection.bitSet.LongBitSet;
import jgine.utils.collection.list.UnorderedIdentityArrayList;

/**
 * Helper class for loading {@link Prefab} files.
 */
public class PrefabLoader {

	private static final Map<String, Object> EMPTY_DATA = new HashMap<>();
	private static final Map<String, List<PrefabData>> STALLED_PARENTS_MAP = new HashMap<>();
	private static final Map<String, List<PrefabData>> STALLED_CHILDS_MAP = new HashMap<>();

	private static record PrefabData(Prefab prefab, Map<String, Object> data) {
	}

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

	public static void save(Prefab prefab, File file) {
		YamlLoader.save(file, saveData(prefab, new HashMap<>()));
	}

	public static void save(Prefab prefab, OutputStream os) {
		YamlLoader.save(os, saveData(prefab, new HashMap<>()));
	}

	public static void save(Prefab prefab, Map<String, Object> data) {
		saveData(prefab, data);
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
					prefab.getChilds().clear();
					addStalledChilds(childName, new PrefabData(prefab, data));
					return prefab;
				}
				prefab.getChilds().add(child);
			}
		}
		loadData(prefab, data);
		Prefab.register(prefab);
		loadStalledParents(prefab.name);
		loadStalledChilds(prefab.name);
		return prefab;
	}

	@SuppressWarnings("unchecked")
	public static void loadData(Prefab prefab, Map<String, Object> data) {
		Object systemData = data.get("systems");
		if (systemData instanceof Map) {
			for (Entry<String, Object> entry : ((Map<String, Object>) systemData).entrySet()) {
				String name = entry.getKey();
				EngineSystem<?, ?> system = Registry.SYSTEM.get(name);
				Object entryData = entry.getValue();
				if (entryData instanceof Map) {
					Map<String, Object> entryMap = (Map<String, Object>) entryData;
					if (system == null)
						system = Registry.SYSTEM.get(ObjectUtils.toString(entryMap.get("system")));
					if (system != null)
						prefab.setSystem(name, system, entryMap);
				} else if (system != null)
					prefab.setSystem(name, system, EMPTY_DATA);
			}
		}

		Object tagData = data.get("tags");
		if (tagData instanceof Number)
			prefab.setTag(((Number) tagData).longValue());
		if (tagData instanceof List) {
			for (String tagName : (List<String>) tagData)
				prefab.setTag(tagName, true);
		}
	}

	public static Map<String, Object> saveData(Prefab prefab, Map<String, Object> data) {
		Map<String, Object> systems = new HashMap<>(prefab.systemSize());
		for (Entry<String, SystemObject> entry : prefab.getSystemEntries()) {
			Map<String, Object> system = new HashMap<>();
			systems.put(entry.getKey(), system);
			entry.getValue().save(system);
		}
		data.put("systems", systems);

		List<String> tags = new ArrayList<>(Prefab.Tag.size());
		data.put("tags", tags);
		long tag = prefab.getTag();
		for (int i = 0; i < Prefab.Tag.size(); i++)
			if (LongBitSet.get(tag, i))
				tags.add(Prefab.Tag.get(i));
		return data;
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
}
