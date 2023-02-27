package org.jgine.core.entity;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.manager.SystemManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

/**
 * Data structure used internally by entities to store their systems.
 */
public class SystemMap {

	private static final SystemObject[] EMPTY_OBJECTS = new SystemObject[0];

	private SystemObject[][] objects;
	private int size;

	public SystemMap() {
		int size = SystemManager.getSize();
		objects = new SystemObject[size][];
		for (int i = 0; i < size; i++)
			objects[i] = EMPTY_OBJECTS;
	}

	public void add(EngineSystem system, SystemObject object) {
		add(system.getId(), object);
	}

	public void add(SystemScene<?, ?> systemScene, SystemObject object) {
		add(systemScene.system.getId(), object);
	}

	public void add(int id, SystemObject object) {
		synchronized (this) {
			SystemObject[] subObjects = get(id);
			SystemObject[] newObjects = Arrays.copyOf(subObjects, subObjects.length + 1);
			newObjects[subObjects.length] = object;
			objects[id] = newObjects;
			size++;
		}
	}

	public void add(EngineSystem system, SystemObject... objects) {
		add(system.getId(), objects);
	}

	public void add(SystemScene<?, ?> systemScene, SystemObject... objects) {
		add(systemScene.system.getId(), objects);
	}

	public void add(int id, SystemObject... objects) {
		synchronized (this) {
			SystemObject[] subObjects = get(id);
			SystemObject[] newObjects = Arrays.copyOf(subObjects, subObjects.length + objects.length);
			System.arraycopy(objects, 0, newObjects, newObjects.length, objects.length);
			this.objects[id] = newObjects;
			size += objects.length;
		}
	}

	public SystemObject[] remove(EngineSystem system) {
		return remove(system.getId());
	}

	public SystemObject[] remove(SystemScene<?, ?> systemScene) {
		return remove(systemScene.system.getId());
	}

	public SystemObject[] remove(int id) {
		synchronized (this) {
			SystemObject[] subObjects = get(id);
			this.objects[id] = EMPTY_OBJECTS;
			size -= subObjects.length;
			return subObjects;
		}
	}

	public int remove(SystemObject object) {
		synchronized (this) {
			int id = -1;
			int subId = -1;
			for (int i = 0; i < objects.length; i++) {
				SystemObject[] subObjects = objects[i];
				if (subObjects != EMPTY_OBJECTS) {
					for (int j = 0; j < subObjects.length; j++) {
						if (subObjects[j] == object) {
							subId = j;
							break;
						}
					}
					if (subId != -1) {
						id = i;
						break;
					}
				}
			}
			if (id == -1)
				return -1;
			SystemObject[] subObjects = objects[id];
			int size = subObjects.length - 1;
			SystemObject[] newObjects = Arrays.copyOf(subObjects, size);
			if (subId != size)
				System.arraycopy(subObjects, subId + 1, newObjects, subId, size - subId);
			objects[id] = newObjects;
			size--;
			return id;
		}
	}

	@Nullable
	public SystemObject get(EngineSystem system, int index) {
		return get(system.getId(), index);
	}

	@Nullable
	public SystemObject get(SystemScene<?, ?> systemScene, int index) {
		return get(systemScene.system.getId(), index);
	}

	@Nullable
	public SystemObject get(int id, int index) {
		synchronized (this) {
			SystemObject[] subObjects = get_(id);
			if (subObjects != EMPTY_OBJECTS)
				return subObjects[index];
			return null;
		}
	}

	public SystemObject[] get(EngineSystem system) {
		return get(system.getId());
	}

	public SystemObject[] get(SystemScene<?, ?> systemScene) {
		return get(systemScene.system.getId());
	}

	public SystemObject[] get(int id) {
		synchronized (this) {
			return get_(id);
		}
	}

	public SystemObject[] get_(int id) {
		if (objects.length <= id) {
			SystemObject[][] newObjects = new SystemObject[SystemManager.getSize()][];
			System.arraycopy(objects, 0, newObjects, 0, objects.length);
			for (int i = objects.length - 1; i < newObjects.length; i++)
				newObjects[i] = EMPTY_OBJECTS;
			objects = newObjects;
		}
		return objects[id];
	}

	public int size() {
		return size;
	}

	/**
	 * Must call hasNext() before every next()
	 * 
	 * @return
	 */
	public Iterator<SystemScene<?, ?>> getSceneIterator(Scene scene) {
		return new SystemSceneIterator(scene);
	}

	/**
	 * Must call hasNext() before every next()
	 * 
	 * @return
	 */
	public Iterator<SystemObject[]> getSystemsIterator() {
		return new SystemsIterator();
	}

	/**
	 * Must call hasNext() before every next()
	 * 
	 * @return
	 */
	public Iterator<SystemObject> getSystemIterator() {
		return new SystemIterator();
	}

	/**
	 * Must call hasNext() before every next()
	 * 
	 * @return
	 */
	public Iterator<Entry<SystemScene<?, ?>, SystemObject[]>> getEntryIterator(Scene scene) {
		return new SystemsEntryIterator(scene);
	}

	private abstract class DataIterator<E> implements Iterator<E> {

		protected int index = -1;

		@Override
		public boolean hasNext() {
			index++;
			for (; index < objects.length; index++) {
				SystemObject[] o = objects[index];
				if (o != EMPTY_OBJECTS)
					return true;
			}
			return false;
		}
	}

	private class SystemSceneIterator extends DataIterator<SystemScene<?, ?>> {

		protected Scene scene;

		public SystemSceneIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public SystemScene<?, ?> next() {
			return scene.getSystem(index);
		}
	}

	private class SystemsIterator extends DataIterator<SystemObject[]> {

		@Override
		public SystemObject[] next() {
			return objects[index];
		}
	}

	private class SystemsEntryIterator extends DataIterator<Entry<SystemScene<?, ?>, SystemObject[]>> {

		protected Scene scene;

		public SystemsEntryIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public Entry<SystemScene<?, ?>, SystemObject[]> next() {
			return new AbstractMap.SimpleEntry<SystemScene<?, ?>, SystemObject[]>(scene.getSystem(index),
					objects[index]);
		}
	}

	private class SystemIterator implements Iterator<SystemObject> {

		protected int index = -1;
		protected int subIndex = -1;

		@Override
		public boolean hasNext() {
			if (subIndex >= 0) {
				SystemObject[] subObjects = objects[index];
				if (++subIndex < subObjects.length)
					return true;
				else
					subIndex = -1;
			}
			index++;
			for (; index < objects.length; index++) {
				SystemObject[] o = objects[index];
				if (o != EMPTY_OBJECTS) {
					subIndex = 0;
					return true;
				}
			}
			return false;
		}

		@Override
		public SystemObject next() {
			return objects[index][subIndex];
		}
	}
}
