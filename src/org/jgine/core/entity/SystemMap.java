package org.jgine.core.entity;

import java.util.AbstractMap;
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
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an {@code SystemMap} instance concurrently, and at
 * least one of the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally. (A structural modification is any operation that
 * adds or deletes one or more elements, or explicitly resizes the backing
 * arrays; merely setting the value of an element is not a structural
 * modification.) This is typically accomplished by synchronizing on some object
 * that naturally encapsulates the map.
 */
public class SystemMap {

	private static final int OBJECTS_SIZE = Entity.MAX_OBJECTS_PER_SYSTEMS + 1;

	private SystemObject[] objects;
	private int[] ids;
	private int size;

	public SystemMap() {
		int size = SystemManager.getSize() * OBJECTS_SIZE;
		objects = new SystemObject[size];
		ids = new int[size];
	}

	public void add(EngineSystem system, SystemObject object) {
		add(system.getId(), object);
	}

	public void add(SystemScene<?, ?> systemScene, SystemObject object) {
		add(systemScene.system.getId(), object);
	}

	public void add(int id, SystemObject object) {
		ensureCapacity(id);
		int systemSize = size(id);
		int index = id * OBJECTS_SIZE + 1 + systemSize;
		objects[index] = object;
		size++;
		setSize(id, systemSize + 1);
	}

	public void add(EngineSystem system, SystemObject... objects) {
		add(system.getId(), objects);
	}

	public void add(SystemScene<?, ?> systemScene, SystemObject... objects) {
		add(systemScene.system.getId(), objects);
	}

	public void add(int id, SystemObject... objects) {
		ensureCapacity(id);
		int systemSize = size(id);
		int index = id * OBJECTS_SIZE + 1 + systemSize;
		int end = (id + 1) * OBJECTS_SIZE;
		int addSize = 0;
		for (; index < end; index++) {
			objects[index] = objects[addSize++];
			if (addSize >= objects.length)
				break;
		}
		size += addSize;
		setSize(id, systemSize + addSize);
	}

	public int remove(EngineSystem system, SystemObject object) {
		return remove(system.getId(), object);
	}

	public int remove(SystemScene<?, ?> systemScene, SystemObject object) {
		return remove(systemScene.system.getId(), object);
	}

	public int remove(int id, SystemObject object) {
		int systemSize = size(id);
		int index = id * OBJECTS_SIZE + 1;
		int end = index + systemSize;
		int objectId = -1;
		for (; index < end; index++) {
			if (objects[index] == object) {
				objectId = ids[index];
				int last = id * OBJECTS_SIZE + systemSize;
				if (index != last) {
					objects[index] = objects[last];
					ids[index] = ids[last];
				}
				objects[last] = null;
				size--;
				setSize(id, systemSize - 1);
				break;
			}
		}
		return objectId;
	}

	public int[] remove(EngineSystem system) {
		return remove(system.getId());
	}

	public int[] remove(SystemScene<?, ?> systemScene) {
		return remove(systemScene.system.getId());
	}

	public int[] remove(int id) {
		int systemSize = size(id);
		int index = id * OBJECTS_SIZE + 1;
		int end = index + systemSize;
		int i = 0;
		int[] result = new int[systemSize];
		for (; index < end; index++) {
			objects[index] = null;
			result[i++] = ids[index];
		}
		size -= systemSize;
		setSize(id, 0);
		return result;
	}

	public SystemObject[] get(EngineSystem system) {
		return get(system.getId());
	}

	public SystemObject[] get(SystemScene<?, ?> systemScene) {
		return get(systemScene.system.getId());
	}

	public SystemObject[] get(int id) {
		int systemSize = size(id);
		int index = id * OBJECTS_SIZE + 1;
		int end = index + systemSize;
		int i = 0;
		SystemObject[] result = new SystemObject[systemSize];
		for (; index < end; index++)
			result[i++] = objects[index];
		return result;
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
		return objects[id * OBJECTS_SIZE + 1 + index];
	}

	public int getId(EngineSystem system, int index) {
		return getId(system.getId(), index);
	}

	public int getId(SystemScene<?, ?> systemScene, int index) {
		return getId(systemScene.system.getId(), index);
	}

	public int getId(int id, int index) {
		return ids[id * OBJECTS_SIZE + 1 + index];
	}

	public int getId(EngineSystem system, SystemObject object) {
		return getId(system.getId(), object);
	}

	public int getId(SystemScene<?, ?> systemScene, SystemObject object) {
		return getId(systemScene.system.getId(), object);
	}

	public int getId(int id, SystemObject object) {
		int index = id * OBJECTS_SIZE + 1;
		int end = index + size(id);
		for (; index < end; index++) {
			if (objects[index] == object)
				return ids[index];
		}
		return -1;
	}

	public void setId(EngineSystem system, SystemObject object, int objectId) {
		setId(system.getId(), object, objectId);
	}

	public void setId(SystemScene<?, ?> systemScene, SystemObject object, int objectId) {
		setId(systemScene.system.getId(), object, objectId);
	}

	public void setId(int id, SystemObject object, int objectId) {
		int index = id * OBJECTS_SIZE + 1;
		int end = index + size(id);
		for (; index < end; index++) {
			if (objects[index] == object) {
				ids[index] = objectId;
				break;
			}
		}
	}

	public int size() {
		return size;
	}

	public int size(EngineSystem system) {
		return size(system.getId());
	}

	public int size(SystemScene<?, ?> systemScene) {
		return size(systemScene.system.getId());
	}

	public int size(int id) {
		return ids[id * OBJECTS_SIZE];
	}

	private void setSize(int id, int size) {
		ids[id * OBJECTS_SIZE] = size;
	}

	protected void ensureCapacity(int minCapacity) {
		int size = minCapacity * OBJECTS_SIZE;
		if (size > objects.length)
			resize(size);
	}

	protected void resize(int size) {
		SystemObject[] newObjects = new SystemObject[size];
		System.arraycopy(objects, 0, newObjects, 0, objects.length);
		objects = newObjects;

		int[] newIds = new int[size];
		System.arraycopy(ids, 0, newIds, 0, ids.length);
		ids = newIds;
	}

	public Iterator<SystemObject> getSystemIterator() {
		return new SystemIterator();
	}

	public Iterator<Integer> getIdIterator() {
		return new IdIterator();
	}

	public Iterator<Entry<SystemScene<?, ?>, SystemObject>> getSystemEntryIterator(Scene scene) {
		return new SystemEntryIterator(scene);
	}

	public Iterator<Entry<SystemScene<?, ?>, Integer>> getIdEntryIterator(Scene scene) {
		return new IdEntryIterator(scene);
	}

	private abstract class DataIterator<E> implements Iterator<E> {

		protected int id;
		protected int index;

		@Override
		public boolean hasNext() {
			for (; id < SystemManager.getSize(); id++) {
				if (index < size(id))
					return true;
				index = 0;
			}
			return false;
		}
	}

	private class SystemIterator extends DataIterator<SystemObject> {

		@Override
		public SystemObject next() {
			return objects[id * OBJECTS_SIZE + 1 + index++];
		}
	}

	private class IdIterator extends DataIterator<Integer> {

		@Override
		public Integer next() {
			return ids[id * OBJECTS_SIZE + 1 + index++];
		}
	}

	private class SystemEntryIterator extends DataIterator<Entry<SystemScene<?, ?>, SystemObject>> {

		protected Scene scene;

		public SystemEntryIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public Entry<SystemScene<?, ?>, SystemObject> next() {
			return new AbstractMap.SimpleEntry<SystemScene<?, ?>, SystemObject>(scene.getSystem(id),
					objects[id * OBJECTS_SIZE + 1 + index++]);
		}
	}

	private class IdEntryIterator extends DataIterator<Entry<SystemScene<?, ?>, Integer>> {

		protected Scene scene;

		public IdEntryIterator(Scene scene) {
			this.scene = scene;
		}

		@Override
		public Entry<SystemScene<?, ?>, Integer> next() {
			return new AbstractMap.SimpleEntry<SystemScene<?, ?>, Integer>(scene.getSystem(id),
					ids[id * OBJECTS_SIZE + 1 + index++]);
		}
	}
}
