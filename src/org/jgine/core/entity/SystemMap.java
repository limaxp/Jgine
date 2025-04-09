package org.jgine.core.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.IntConsumer;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * Data structure used internally by {@link Entity} class to store their
 * {@link EngineSystem}<code>s</code>.
 */
public class SystemMap {

	private final Int2IntMap data;
	private int size;

	public SystemMap() {
		data = new Int2IntOpenHashMap(EngineSystem.size() + 16);
	}

	public int add(int system, int value) {
		synchronized (data) {
			int systemSize = size(system);
			setIntern(system, systemSize, value);
			setSize(system, systemSize + 1);
			size++;
			return systemSize;
		}
	}

	public void add(int system, int... values) {
		synchronized (data) {
			int systemSize = size(system);
			int addSize = values.length;
			for (int i = 0; i < addSize; i++)
				setIntern(system, systemSize + i, values[i]);
			setSize(system, systemSize + addSize);
			size += addSize;
		}
	}

	public void set_(int system, int index, int value) {
		synchronized (data) {
			setIntern(system, index, value);
		}
	}

	public boolean set(int system, int oldValue, int newValue) {
		// TODO Inefficient
		synchronized (data) {
			int systemSize = size(system);
			for (int i = 0; i < systemSize; i++) {
				if (getIntern(system, i) == oldValue) {
					setIntern(system, i, newValue);
					return true;
				}
			}
			return false;
		}
	}

	public boolean remove(int system, int value) {
		// TODO Inefficient
		synchronized (data) {
			int systemSize = size(system);
			for (int i = 0; i < systemSize; i++) {
				if (getIntern(system, i) == value) {
					removeIntern(system, i);
					return true;
				}
			}
			return false;
		}
	}

	public <T extends SystemObject> int remove(SystemScene<?, T> system, T value) {
		// TODO Inefficient
		synchronized (data) {
			int systemId = system.id;
			int systemSize = size(systemId);
			for (int i = 0; i < systemSize; i++) {
				if (system.get(getIntern(systemId, i)) == value)
					return removeIntern(systemId, i);
			}
			return -1;
		}
	}

	public void remove(int system) {
		synchronized (data) {
			int systemSize = size(system);
			for (int i = 0; i < systemSize; i++)
				data.remove(id(system, i + 1));
			setSize(system, 0);
			size -= systemSize;
		}
	}

	public int get(int system, int index) {
		synchronized (data) {
			return getIntern(system, index);
		}
	}

	public void forEach(int system, IntConsumer func) {
		synchronized (data) {
			int systemSize = size(system);
			for (int i = 0; i < systemSize; i++)
				func.accept(getIntern(system, i));
		}
	}

	public void forEach(IntConsumer func) {
		synchronized (data) {
			int size = EngineSystem.size();
			for (int system = 0; system < size; system++) {
				int systemSize = size(system);
				for (int i = 0; i < systemSize; i++)
					func.accept(getIntern(system, i));
			}
		}
	}

	public void forEach(Scene scene, SystemMapConsumer func) {
		synchronized (data) {
			int size = EngineSystem.size();
			for (int system = 0; system < size; system++) {
				int systemSize = size(system);
				SystemScene<?, ?> systemScene = scene.getSystem(system);
				for (int i = 0; i < systemSize; i++)
					func.accept(systemScene, getIntern(system, i));
			}
		}
	}

	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		int systemCount = EngineSystem.size();
		out.writeInt(systemCount);
		for (int system = 0; system < systemCount; system++) {
			int systemSize = size(system);
			out.writeInt(systemSize);
			for (int i = 0; i < systemSize; i++)
				out.writeInt(getIntern(system, i));
		}
	}

	public void load(DataInput in, Entity entity) throws IOException {
		Scene scene = entity.scene;
		this.size = in.readInt();
		int systemCount = in.readInt();
		for (int system = 0; system < systemCount; system++) {
			SystemScene<?, ?> systemScene = scene.getSystem(system);
			int systemSize = in.readInt();
			setSize(system, systemSize);
			for (int i = 0; i < systemSize; i++) {
				int objectId = in.readInt();
				setIntern(system, i, objectId);
				systemScene.relink(objectId, entity);
				systemScene.init_(entity, systemScene.get(objectId));
			}
		}
	}

	private void setIntern(int system, int index, int value) {
		data.put(id(system, index + 1), value);
	}

	private int removeIntern(int system, int index) {
		int result = id(system, index + 1);
		data.remove(result);
		setSize(system, size(system) - 1);
		size--;
		return result;
	}

	private int getIntern(int system, int index) {
		return data.getOrDefault(id(system, index + 1), -1);
	}

	private void setSize(int system, int size) {
		data.put(id(system, 0), size);
	}

	public int size(int system) {
		return (int) data.getOrDefault(id(system, 0), 0);
	}

	public int size() {
		return size;
	}

	private static int id(int system, int index) {
		return 0x00000000 | system << 16 | index;
	}

	@FunctionalInterface
	public static interface SystemMapConsumer {

		public void accept(SystemScene<?, ?> scene, int id);
	}
}