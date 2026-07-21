package jgine.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import jgine.system.EngineSystem;
import jgine.system.SystemObject;
import jgine.utils.StringUtils;
import jgine.utils.collection.bitSet.LongBitSet;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.loader.PrefabLoader;
import jgine.utils.loader.ResourceManager;

/**
 * Blueprint for a {@link Entity}. <strong>Not thread save!</strong>
 * 
 * <pre>
 * Stores:
 * - int id 
 * - String name
 * - {@link EngineSystem}<code>s</code>
 * - child {@link Prefab}<code>s</code>
 * - {@link Tag}
 * </pre>
 * 
 * Use create() methods to build a {@link Entity}.
 * <p>
 * Prefabs are usually written as YAML files with the .prefab extension and
 * loaded with the {@link ResourceManager}. The prefab name becomes the file
 * name without the extension.
 * 
 * <pre>
YAML example:

parents:
  - parentName1
  - parentName2
  - ...
childs:
  - childName1
  - childName2
  - ...
tags:
  - tag1
  - tag2
  - ...
systems:
  systemName1:
    systemData
  systemName2:
    systemData
  ...:
 * </pre>
 */
public final class Prefab {

	private static final List<Prefab> LIST = new UnorderedIdentityArrayList<Prefab>();
	private static final Map<String, Prefab> NAME_MAP = new HashMap<String, Prefab>(10000);
	private static final Map<Integer, Prefab> ID_MAP = new HashMap<Integer, Prefab>(10000);

	static final Prefab NONE = register(new Prefab("none"));

	public final int id;
	public final String name;
	private final Map<String, SystemObject> systemMap;
	private final List<SystemObject> systemList;
	private final List<Prefab> parents;
	private final List<Prefab> childs;
	private long tag;

	public Prefab(String name) {
		id = name.hashCode();
		this.name = name;
		systemMap = new HashMap<>();
		systemList = new UnorderedIdentityArrayList<>();
		parents = new UnorderedIdentityArrayList<>();
		childs = new UnorderedIdentityArrayList<>();
	}

	public void clear() {
		systemMap.clear();
		systemList.clear();
		parents.clear();
		childs.clear();
		tag = 0L;
	}

	public void setSystem(String name, EngineSystem<?, ?> system, Map<String, Object> data) {
		SystemObject saved = systemMap.get(name);
		if (saved != null)
			saved.load(data);
		else
			setSystem(name, system.load(data));
	}

	public void setSystem(String name, SystemObject value) {
		systemMap.put(name, value);
		systemList.add(value);
	}

	@Nullable
	public SystemObject removeSystem(String name) {
		SystemObject result = systemMap.remove(name);
		systemList.remove(result);
		return result;
	}

	@Nullable
	public SystemObject getSystem(String name) {
		return systemMap.get(name);
	}

	@Nullable
	public SystemObject getSystem(int system) {
		int size = systemList.size();
		for (int i = 0; i < size; i++)
			if (systemList.get(i).system() == system)
				return systemList.get(i);
		return null;
	}

	public boolean hasSystem(int system) {
		return getSystem(system) != null;
	}

	public List<SystemObject> getSystems() {
		return Collections.unmodifiableList(systemList);
	}

	public Set<String> getSystemNames() {
		return Collections.unmodifiableSet(systemMap.keySet());
	}

	public Set<Entry<String, SystemObject>> getSystemEntries() {
		return Collections.unmodifiableSet(systemMap.entrySet());
	}

	public int systemSize() {
		return systemMap.size();
	}

	public void addParent(Prefab parent) {
		parents.add(parent);
		Map<String, Object> map = new HashMap<>();
		PrefabLoader.saveData(parent, map);
		PrefabLoader.loadData(this, map);
		tag = LongBitSet.or(tag, parent.tag);
	}

	public List<Prefab> getParents() {
		return Collections.unmodifiableList(parents);
	}

	public List<Prefab> getChilds() {
		return childs;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public long getTag() {
		return tag;
	}

	public void setTag(String name, boolean value) {
		setTag(Tag.get(name), value);
	}

	public void setTag(int id, boolean value) {
		tag = LongBitSet.set(tag, id, value);
	}

	public boolean getTag(String name) {
		return getTag(Tag.get(name));
	}

	public boolean getTag(int id) {
		return LongBitSet.get(tag, id);
	}

	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name + ", parents=" + StringUtils.prefabsToString(parents) + ", childs="
				+ StringUtils.prefabsToString(childs) + ", systems=" + getSystemNames() + "]";
	}

	public Entity create(Scene scene) {
		return create(scene, new Entity(scene));
	}

	public Entity create(int id, Scene scene) {
		return create(scene, new Entity(id, scene));
	}

	private Entity create(Scene scene, Entity entity) {
		entity.setPrefab(this);
		for (SystemObject system : systemList)
			entity.add(system.system(), (SystemObject) system.clone());
		for (Prefab child : childs)
			child.create(scene).getTransform().setParent(entity.getTransform());
		return entity;
	}

	@Nullable
	public static Prefab get(String name) {
		return NAME_MAP.get(name);
	}

	@Nullable
	public static Prefab get(int id) {
		return ID_MAP.get(id);
	}

	public static List<Prefab> values() {
		return Collections.unmodifiableList(LIST);
	}

	public static Prefab register(Prefab prefab) {
		LIST.add(prefab);
		NAME_MAP.put(prefab.name, prefab);
		ID_MAP.put(prefab.id, prefab);
		return prefab;
	}

	public static Prefab unregister(Prefab prefab) {
		LIST.remove(prefab);
		NAME_MAP.remove(prefab.name);
		ID_MAP.remove(prefab.id);
		return prefab;
	}

	public static void unregisterAll() {
		LIST.clear();
		NAME_MAP.clear();
		ID_MAP.clear();
	}

	/**
	 * Tags that can be attached to an {@link Prefab} to mark it with identifiers.
	 * <p>
	 * Only supports up to 64 tags since its internally stored as a long! Tags must
	 * be registered here before using them!
	 * <p>
	 */
	public static class Tag {

		private static final String[] TAGS = new String[LongBitSet.MAX_SIZE];
		private static final Object2IntMap<String> NAME_MAP = new Object2IntOpenHashMap<String>(64);
		private static int size;

		public static int register(String name) {
			if (size >= LongBitSet.MAX_SIZE - 1)
				throw new ArrayIndexOutOfBoundsException(
						"Maximum tag size reached! Only up to " + LongBitSet.MAX_SIZE + " allowed!");
			int index = size++;
			TAGS[index] = name;
			NAME_MAP.put(name, index);
			return index;
		}

		@Nullable
		public static String get(int index) {
			return TAGS[index];
		}

		public static int get(String name) {
			return NAME_MAP.getOrDefault(name, -1);
		}

		public static int size() {
			return size;
		}
	}
}
