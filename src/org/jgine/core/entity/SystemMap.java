package org.jgine.core.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.IntConsumer;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

/**
 * Data structure used internally by {@link Entity} class to store their
 * {@link EngineSystem}<code>s</code>.
 */
public class SystemMap {

	private static final int[] NULL_ARRAY = new int[] { 0, -1 };

	private int[][] data;
	private int size;

	public SystemMap() {
		int systemCount = EngineSystem.size();
		data = new int[systemCount][];
		for (int i = 0; i < systemCount; i++)
			data[i] = NULL_ARRAY;
	}

	public void add(int id, int objectId) {
		synchronized (data) {
			int systemSize = size(id) + 1;
			ensureCapacity(id, systemSize + 1);
			data[id][systemSize] = objectId;
			setSize(id, systemSize);
			size++;
		}
	}

	public void add(int id, int... objectIds) {
		synchronized (data) {
			int systemSize = size(id) + 1;
			int addSize = objectIds.length;
			ensureCapacity(id, systemSize + addSize);
			int[] ids = data[id];
			for (int i = 0; i < addSize; i++)
				ids[systemSize + i] = objectIds[i];
			setSize(id, systemSize - 1 + addSize);
			size += addSize;
		}
	}

	public boolean set(int id, int oldId, int newId) {
		synchronized (data) {
			int[] ids = data[id];
			int systemSize = size(id) + 1;
			for (int i = 1; i < systemSize; i++)
				if (ids[i] == oldId) {
					ids[i] = newId;
					return true;
				}
			return false;
		}
	}

	public boolean remove(int id, int objectId) {
		synchronized (data) {
			int[] ids = data[id];
			int systemSize = size(id) + 1;
			for (int i = 1; i < systemSize; i++) {
				if (ids[i] == objectId) {
					remove(ids, i);
					return true;
				}
			}
			return false;
		}
	}

	public <T extends SystemObject> int remove(SystemScene<?, T> systemScene, T object) {
		synchronized (data) {
			int id = systemScene.id;
			int[] ids = data[id];
			int systemSize = size(id) + 1;
			for (int i = 1; i < systemSize; i++) {
				if (systemScene.getObject(ids[i]) == object) {
					int result = ids[i];
					remove(ids, i);
					return result;
				}
			}
			return -1;
		}
	}

	private void remove(int[] ids, int index) {
		int systemSize = ids[0];
		if (index != systemSize)
			ids[index] = ids[systemSize];
		ids[systemSize] = -1;
		ids[0] = systemSize - 1;
		size--;
	}

	public void remove(int id) {
		synchronized (data) {
			int systemSize = size(id);
			data[id] = NULL_ARRAY;
			size -= systemSize;
		}
	}

	public int get(int id, int index) {
		return data[id][index + 1];
	}

	public <T extends SystemObject> void forEach(int id, IntConsumer func) {
		synchronized (data) {
			int[] ids = data[id];
			int systemSize = size(id) + 1;
			for (int i = 1; i < systemSize; i++)
				func.accept(ids[i]);
		}
	}

	public <T extends SystemObject> void forEach(IntConsumer func) {
		synchronized (data) {
			int size = EngineSystem.size();
			for (int id = 0; id < size; id++) {
				int[] ids = data[id];
				int systemSize = size(id) + 1;
				for (int i = 1; i < systemSize; i++)
					func.accept(ids[i]);
			}
		}
	}

	public <T extends SystemObject> void forEach(Scene scene, SystemMapConsumer func) {
		synchronized (data) {
			int size = EngineSystem.size();
			for (int id = 0; id < size; id++) {
				int[] ids = data[id];
				int systemSize = size(id) + 1;
				SystemScene<?, ?> systemScene = scene.getSystem(id);
				for (int i = 1; i < systemSize; i++)
					func.accept(systemScene, ids[i]);
			}
		}
	}

	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		int systemCount = data.length;
		out.writeInt(systemCount);
		for (int id = 0; id < systemCount; id++) {
			int[] ids = data[id];
			int systemSize = size(id);
			out.writeInt(systemSize);
			systemSize++;
			for (int i = 1; i < systemSize; i++)
				out.writeInt(ids[i]);
		}
	}

	public void load(DataInput in, Entity entity) throws IOException {
		Scene scene = entity.scene;
		this.size = in.readInt();
		int systemCount = in.readInt();
		for (int id = 0; id < systemCount; id++) {
			SystemScene<?, ?> systemScene = scene.getSystem(id);
			int[] ids = data[id];
			int systemSize = in.readInt() + 1;
			ensureCapacity(id, systemSize);
			for (int i = 1; i < systemSize; i++) {
				int objectId = in.readInt();
				ids[i] = objectId;
				systemScene.relink(objectId, entity);
				systemScene.initObject_(entity, systemScene.getObject(objectId));
			}
			setSize(id, systemSize - 1);
		}
	}

	public int size() {
		return size;
	}

	public int size(int id) {
		return data[id][0];
	}

	private void setSize(int id, int size) {
		data[id][0] = size;
	}

	private void ensureCapacity(int id, int minCapacity) {
		int[] ids = data[id];
		int length = ids.length;
		if (minCapacity > length | ids == NULL_ARRAY) {
			int newLength = Math.max(length * 2, minCapacity);
			int[] newIds = new int[newLength];
			System.arraycopy(ids, 0, newIds, 0, length);
			for (int i = length; i < newLength; i++)
				newIds[i] = -1;
			data[id] = newIds;
		}
	}

	@FunctionalInterface
	public static interface SystemMapConsumer {

		public void accept(SystemScene<?, ?> scene, int id);
	}

}
