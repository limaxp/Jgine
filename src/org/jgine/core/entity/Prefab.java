package org.jgine.core.entity;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.bitSet.LongBitSet;
import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.TransformData;
import org.jgine.core.manager.ResourceManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.utils.Reflection;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * A blueprint for a {@link Entity}. Stores info about name, id,
 * {@link Transform}, {@link EntityTag}, used
 * {@link EngineSystems}<code>s</code> and child prefabs. Also contains a
 * Map<Object, Object> to store any extra data. Use create() methods to build a
 * {@link Entity}.
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
data:
  dataName1: data
  dataName2: data
  ...:
transform:
  position: xyzValue
  position: [x, y, z]
  position:
    x: xValue
    y: yValue
    z: zValue
  rotation: xyzValue
  rotation: [x, y, z]
  rotation:
    x: xValue
    y: yValue
    z: zValue
  scale: xyzValue
  scale: [x, y, z]
  scale:
    x: xValue
    y: yValue
    z: zValue
systems:
  systemName1:
    systemData
  systemName2:
    systemData
  ...:
 * </pre>
 */
public class Prefab {

	public static final int MAX_SYSTEMS = 20;

	private static final SystemObject[] EMPTY_OBJECTS = new SystemObject[0];

	public final String name;
	public final int id;
	public final TransformData transform;
	private EngineSystem[] systems;
	private SystemObject[][] objects;
	private String[][] names;
	int size;
	private final Map<Object, Object> data;
	private final List<Prefab> childs;
	private LongBitSet tag;

	public Prefab(String name) {
		this.name = name;
		id = name.hashCode();
		systems = new EngineSystem[MAX_SYSTEMS];
		objects = new SystemObject[MAX_SYSTEMS][];
		names = new String[MAX_SYSTEMS][];
		data = new HashMap<Object, Object>();
		childs = new UnorderedIdentityArrayList<Prefab>();
		transform = new TransformData();
		tag = new LongBitSet();
	}

	public final void set(EngineSystem system, String name, SystemObject object) {
		int index = indexOf(system);
		if (index >= 0) {
			String[] subNames = names[index];
			for (int i = 0; i < subNames.length; i++) {
				if (name.equals(subNames[i])) {
					objects[index][i] = object;
					return;
				}
			}
			SystemObject[] subObjects = objects[index];
			String[] newNames = Arrays.copyOf(subNames, subNames.length + 1);
			newNames[subNames.length] = name;
			names[index] = newNames;
			SystemObject[] newObjects = Arrays.copyOf(subObjects, subObjects.length + 1);
			newObjects[subObjects.length] = object;
			objects[index] = newObjects;
			return;
		}
		ensureCapacity(size);
		systems[size] = system;
		names[size] = new String[] { name };
		objects[size] = new SystemObject[] { object };
		size++;
	}

	@Nullable
	public final SystemObject remove(EngineSystem system, String name) {
		int index = indexOf(system);
		if (index >= 0) {
			String[] subNames = names[index];
			for (int i = 0; i < subNames.length; i++) {
				if (name.equals(subNames[i])) {
					SystemObject[] subObjects = objects[index];
					SystemObject result = subObjects[i];
					int newSize = subNames.length - 1;
					if (newSize == 0) {
						if (index != --size) {
							System.arraycopy(systems, index + 1, systems, index, size - index);
							System.arraycopy(names, index + 1, names, index, size - index);
							System.arraycopy(objects, index + 1, objects, index, size - index);
						}
						systems[size] = null;
						names[size] = null;
						objects[size] = null;

					} else {
						String[] newNames = Arrays.copyOf(subNames, newSize);
						SystemObject[] newObjects = Arrays.copyOf(subObjects, newSize);
						if (i != newSize) {
							System.arraycopy(subNames, i + 1, newNames, i, newSize - i);
							System.arraycopy(subObjects, i + 1, newObjects, i, newSize - i);
						}
						names[index] = newNames;
						objects[index] = newObjects;
					}
					return result;
				}
			}
		}
		return null;
	}

	public final SystemObject[] remove(EngineSystem system) {
		int index = indexOf(system);
		if (index >= 0) {
			SystemObject[] result = objects[index];
			if (index != --size) {
				System.arraycopy(systems, index + 1, systems, index, size - index);
				System.arraycopy(names, index + 1, names, index, size - index);
				System.arraycopy(objects, index + 1, objects, index, size - index);
			}
			systems[size] = null;
			names[size] = null;
			objects[size] = null;
			return result;
		}
		return EMPTY_OBJECTS;
	}

	@Nullable
	public final SystemObject get(EngineSystem system, String name) {
		int index = indexOf(system);
		if (index >= 0) {
			String[] subNames = names[index];
			for (int i = 0; i < subNames.length; i++)
				if (name.equals(subNames[i]))
					return objects[index][i];
		}
		return null;
	}

	@Nullable
	public final SystemObject get(EngineSystem system, int index) {
		SystemObject[] subObjects = get(system);
		if (subObjects != EMPTY_OBJECTS)
			return subObjects[index];
		return null;
	}

	public final SystemObject[] get(EngineSystem system) {
		int index = indexOf(system);
		if (index >= 0)
			return objects[index];
		return EMPTY_OBJECTS;
	}

	public final boolean has(EngineSystem system) {
		return indexOf(system) != -1;
	}

	public final Iterator<EngineSystem> systemIterator() {
		return new SystemIterator();
	}

	public final Iterator<String> nameIterator() {
		return new NameIterator();
	}

	public final Iterator<SystemObject> objectIterator() {
		return new ObjectIterator();
	}

	public final void addParent(Prefab parent) {
		EngineSystem[] parentSystems = parent.systems;
		String[][] parentNames = parent.names;
		SystemObject[][] parentObjects = parent.objects;
		for (int i = 0; i < parent.size; i++) {
			EngineSystem parentSystem = parentSystems[i];
			String[] subNames = parentNames[i];
			SystemObject[] subObjects = parentObjects[i];
			for (int j = 0; j < subNames.length; j++) {
				set(parentSystem, subNames[j], subObjects[j].copy());
			}
		}
		setData(parent.data);
		tag.or(parent.tag.getBits());
	}

	public final void removeParent(Prefab parent) {
		EngineSystem[] parentSystems = parent.systems;
		String[][] parentNames = parent.names;
		for (int i = 0; i < parent.size; i++) {
			EngineSystem parentSystem = parentSystems[i];
			String[] subNames = parentNames[i];
			for (int j = 0; j < subNames.length; j++)
				remove(parentSystem, subNames[j]);
		}
		removeData(parent.data);
		tag.andNot(parent.tag.getBits());
	}

	private final int indexOf(EngineSystem system) {
		for (int i = 0; i < size; i++) {
			if (system == systems[i])
				return i;
		}
		return -1;
	}

	private final void ensureCapacity(int minCapacity) {
		int length = systems.length;
		if (minCapacity > length)
			resize(Math.max(length * 2, minCapacity));
	}

	private final void resize(int size) {
		EngineSystem[] newSystems = new EngineSystem[size];
		System.arraycopy(systems, 0, newSystems, 0, this.size);
		systems = newSystems;

		SystemObject[][] newObjects = new SystemObject[size][];
		System.arraycopy(objects, 0, newObjects, 0, this.size);
		objects = newObjects;

		String[][] newNames = new String[size][];
		System.arraycopy(names, 0, newNames, 0, this.size);
		names = newNames;
	}

	public final void addChild(Prefab child) {
		childs.add(child);
	}

	public final void removeChild(Prefab child) {
		childs.remove(child);
	}

	public final void isChild(Prefab child) {
		childs.contains(child);
	}

	public final void clearChilds() {
		childs.clear();
	}

	public final void clear() {
		for (int i = 0; i < size; i++) {
			systems[i] = null;
			objects[i] = null;
			names[i] = null;
		}
		size = 0;
		data.clear();
		childs.clear();
	}

	@Nullable
	public final Object setData(Object identifier, Object data) {
		return this.data.put(identifier, data);
	}

	public final void setData(Map<? extends Object, ? extends Object> map) {
		this.data.putAll(map);
	}

	@Nullable
	public final Object removeData(Object identifier) {
		return data.remove(identifier);
	}

	public final void removeData(Map<? extends Object, ? extends Object> map) {
		for (Object identifier : map.keySet())
			data.remove(identifier);
	}

	@Nullable
	public final Object getData(Object identifier) {
		return data.get(identifier);
	}

	public final LongBitSet getTag() {
		return tag;
	}

	public final void setTagBits(long bits) {
		tag.setBits(bits);
	}

	public final long getTagBits() {
		return tag.getBits();
	}

	public final void setTag(String name) {
		tag.set(EntityTag.get(name));
	}

	public final void setTag(int id) {
		tag.set(id);
	}

	public final boolean getTag(String name) {
		return tag.get(EntityTag.get(name));
	}

	public final boolean getTag(int id) {
		return tag.get(id);
	}

	public final void setTag(String... names) {
		for (String name : names)
			setTag(name);
	}

	public final void setTag(int... ids) {
		for (int id : ids)
			setTag(id);
	}

	public final boolean getTag(String... names) {
		long result = 0L;
		for (String name : names)
			result = LongBitSet.set(result, EntityTag.get(name));
		return LongBitSet.and(result, tag.getBits()) == tag.getBits();
	}

	public final boolean getTag(int... ids) {
		long result = 0L;
		for (int id : ids)
			result = LongBitSet.set(result, id);
		return LongBitSet.and(result, tag.getBits()) == tag.getBits();
	}

	public final Entity create(Scene scene) {
		return create_(scene, new Entity(scene, transform));
	}

	public final Entity create(Scene scene, Vector2f position) {
		return create_(scene, new Entity(scene, position.x, position.y, 0, transform.rotX, transform.rotY,
				transform.rotZ, transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, Vector2f position, Vector2f rotation, Vector2f scale) {
		return create_(scene, new Entity(scene, position, rotation, scale));
	}

	public final Entity create(Scene scene, float posX, float posY) {
		return create_(scene, new Entity(scene, posX, posY, 0, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, float posX, float posY, float rotX, float rotY, float scaleX,
			float scaleY) {
		return create_(scene, new Entity(scene, posX, posY, 0, rotX, rotY, 0, scaleX, scaleY, 1));
	}

	public final Entity create(Scene scene, Vector3f position) {
		return create_(scene, new Entity(scene, position.x, position.y, position.z, transform.rotX, transform.rotY,
				transform.rotZ, transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, Vector3f position, Vector3f rotation, Vector3f scale) {
		return create_(scene, new Entity(scene, position, rotation, scale));
	}

	public final Entity create(Scene scene, float posX, float posY, float posZ) {
		return create_(scene, new Entity(scene, posX, posY, posZ, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, float posX, float posY, float posZ, float rotX, float rotY, float rotZ,
			float scaleX, float scaleY, float scaleZ) {
		return create_(scene, new Entity(scene, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ));
	}

	public final Entity create(int id, Scene scene, float posX, float posY, float posZ, float rotX, float rotY,
			float rotZ, float scaleX, float scaleY, float scaleZ) {
		return create_(scene, new Entity(id, scene, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ));
	}

	private final Entity create_(Scene scene, Entity entity) {
		entity.setPrefab(this);
		for (int i = 0; i < size; i++) {
			EngineSystem system = systems[i];
			SystemObject[] subObjects = objects[i];
			for (int j = 0; j < subObjects.length; j++)
				entity.addSystem(system, subObjects[j].copy());
		}
		for (Prefab child : childs)
			child.create(scene).setParent(entity);
		return entity;
	}

	public final <T extends Entity> T create(Scene scene, T entity) {
		entity.setPrefab(this);
		for (int i = 0; i < size; i++) {
			EngineSystem system = systems[i];
			SystemObject[] subObjects = objects[i];
			for (int j = 0; j < subObjects.length; j++)
				entity.addSystem(system, subObjects[j].copy());
		}
		if (!childs.isEmpty()) {
			Constructor<? extends Entity> constructor = Reflection.getDeclaredConstructor(entity.getClass(),
					new Class[] { Scene.class });
			for (Prefab child : childs)
				child.create(scene, Reflection.newInstance(constructor, scene)).setParent(entity);
		}
		return entity;
	}

	@Nullable
	public static Prefab get(String name) {
		return PrefabManager.get(name);
	}

	@Nullable
	public static Prefab get(int id) {
		return PrefabManager.get(id);
	}

	public static Collection<Prefab> values() {
		return PrefabManager.values();
	}

	private abstract class SubDataIterator<E> implements Iterator<E> {

		protected int index;
		protected int subIndex;

		@Override
		public boolean hasNext() {
			for (; index < objects.length; index++) {
				if (subIndex < names[index].length)
					return true;
				subIndex = 0;
			}
			return false;
		}
	}

	private class NameIterator extends SubDataIterator<String> {

		@Override
		public String next() {
			return names[index][subIndex++];
		}
	}

	private class ObjectIterator extends SubDataIterator<SystemObject> {

		@Override
		public SystemObject next() {
			return objects[index][subIndex++];
		}
	}

	private class SystemIterator implements Iterator<EngineSystem> {

		protected int index;

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public EngineSystem next() {
			return systems[index++];
		}
	}
}
