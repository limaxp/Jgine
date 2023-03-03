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
 * Data structure used internally by {@link Entity} class to store their
 * {@link EngineSystem}<code>s</code>.
 */
public class SystemMap {

	private static final SystemObject[] EMPTY_OBJECTS = new SystemObject[0];
	private static final int[] EMPTY_IDS = new int[0];

	private SystemObject[][] objects;
	private int[][] ids;
	private int size;

	public SystemMap() {
		int size = SystemManager.getSize();
		objects = new SystemObject[size][];
		for (int i = 0; i < size; i++)
			objects[i] = EMPTY_OBJECTS;
		ids = new int[size][];
		for (int i = 0; i < size; i++)
			ids[i] = EMPTY_IDS;
	}

	public void setId(EngineSystem system, SystemObject object, int objectId) {
		setId(system.getId(), object, objectId);
	}

	public void setId(SystemScene<?, ?> systemScene, SystemObject object, int objectId) {
		setId(systemScene.system.getId(), object, objectId);
	}

	public void setId(int id, SystemObject object, int objectId) {
		SystemObject[] subObjects = get_(id);
		for (int i = 0; i < subObjects.length; i++) {
			if (subObjects[i] == object)
				ids[id][i] = objectId;
		}
	}

	public void add(EngineSystem system, SystemObject object) {
		add(system.getId(), object);
	}

	public void add(SystemScene<?, ?> systemScene, SystemObject object) {
		add(systemScene.system.getId(), object);
	}

	public void add(int id, SystemObject object) {
		synchronized (this) {
			SystemObject[] subObjects = get_(id);
			SystemObject[] newObjects = Arrays.copyOf(subObjects, subObjects.length + 1);
			newObjects[subObjects.length] = object;
			objects[id] = newObjects;
			ids[id] = Arrays.copyOf(ids[id], subObjects.length + 1);
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
			SystemObject[] subObjects = get_(id);
			SystemObject[] newObjects = Arrays.copyOf(subObjects, subObjects.length + objects.length);
			System.arraycopy(objects, 0, newObjects, newObjects.length, objects.length);
			this.objects[id] = newObjects;
			ids[id] = Arrays.copyOf(ids[id], subObjects.length + objects.length);
			size += objects.length;
		}
	}

	public int[] remove(EngineSystem system) {
		return remove(system.getId());
	}

	public int[] remove(SystemScene<?, ?> systemScene) {
		return remove(systemScene.system.getId());
	}

	public int[] remove(int id) {
		synchronized (this) {
			SystemObject[] subObjects = get_(id);
			int[] subIds = ids[id];
			objects[id] = EMPTY_OBJECTS;
			ids[id] = EMPTY_IDS;
			size -= subObjects.length;
			return subIds;
		}
	}

	public int remove(EngineSystem system, SystemObject object) {
		return remove(system.getId(), object);
	}

	public int remove(SystemScene<?, ?> systemScene, SystemObject object) {
		return remove(systemScene.system.getId(), object);
	}

	public int remove(int id, SystemObject object) {
		synchronized (this) {
			SystemObject[] subObjects = get_(id);
			for (int i = 0; i < subObjects.length; i++) {
				if (subObjects[i] == object) {
					int objectId = ids[id][i];
					int size = subObjects.length - 1;
					SystemObject[] newObjects = Arrays.copyOf(subObjects, size);
					int[] newIds = Arrays.copyOf(ids[id], size);
					if (i != size) {
						System.arraycopy(subObjects, i + 1, newObjects, i, size - i);
						System.arraycopy(ids[id], i + 1, newIds, i, size - i);
					}
					objects[id] = newObjects;
					ids[id] = newIds;
					size--;
					return objectId;
				}
			}
		}
		return -1;
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

			int[][] newIds = new int[SystemManager.getSize()][];
			System.arraycopy(ids, 0, newIds, 0, ids.length);
			for (int i = ids.length - 1; i < newIds.length; i++)
				newIds[i] = EMPTY_IDS;
			ids = newIds;
		}
		return objects[id];
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

	public int size() {
		return size;
	}

	public Iterator<SystemScene<?, ?>> getSceneIterator(Scene scene) {
		return new SystemSceneIterator(scene);
	}

	public Iterator<SystemObject[]> getSystemsIterator() {
		return new SystemsIterator();
	}

	public Iterator<SystemObject> getSystemIterator() {
		return new SystemIterator();
	}

	public Iterator<Integer> getIdIterator() {
		return new IdIterator();
	}

	public Iterator<Entry<SystemScene<?, ?>, SystemObject[]>> getSystemEntryIterator(Scene scene) {
		return new SystemsEntryIterator(scene);
	}

	public Iterator<Entry<SystemScene<?, ?>, int[]>> getIdEntryIterator(Scene scene) {
		return new IdEntryIterator(scene);
	}

	private abstract class DataIterator<E> implements Iterator<E> {

		protected int index;

		@Override
		public boolean hasNext() {
			for (; index < objects.length; index++) {
				if (objects[index] != EMPTY_OBJECTS)
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
			return scene.getSystem(index++);
		}
	}

	private class SystemsIterator extends DataIterator<SystemObject[]> {

		@Override
		public SystemObject[] next() {
			return objects[index++];
		}
	}

	private class SystemsEntryIterator extends DataIterator<Entry<SystemScene<?, ?>, SystemObject[]>> {

		protected Scene scene;

		public SystemsEntryIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public Entry<SystemScene<?, ?>, SystemObject[]> next() {
			int i = index++;
			return new AbstractMap.SimpleEntry<SystemScene<?, ?>, SystemObject[]>(scene.getSystem(i), objects[i]);
		}
	}

	private class IdEntryIterator extends DataIterator<Entry<SystemScene<?, ?>, int[]>> {

		protected Scene scene;

		public IdEntryIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public Entry<SystemScene<?, ?>, int[]> next() {
			int i = index++;
			return new AbstractMap.SimpleEntry<SystemScene<?, ?>, int[]>(scene.getSystem(i), ids[i]);
		}
	}

	private abstract class SubDataIterator<E> implements Iterator<E> {

		protected int index;
		protected int subIndex;

		@Override
		public boolean hasNext() {
			for (; index < objects.length; index++) {
				if (subIndex < objects[index].length)
					return true;
				subIndex = 0;
			}
			return false;
		}
	}

	private class SystemIterator extends SubDataIterator<SystemObject> {

		protected int index;
		protected int subIndex;

		@Override
		public SystemObject next() {
			return objects[index][subIndex++];
		}
	}

	private class IdIterator extends SubDataIterator<Integer> {

		protected int index;
		protected int subIndex;

		@Override
		public Integer next() {
			return ids[index][subIndex++];
		}
	}
}
