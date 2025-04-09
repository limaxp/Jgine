package org.jgine.core.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.IntConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Data structure used internally by {@link Entity} class to store their
 * {@link EngineSystem}<code>s</code>.
 */
public class SystemMap {

	private static final int INITAL_SIZE = 16;

	private final Int2IntMap data;
	private int size;
	private final Int2ObjectMap<SystemObject> cache;
	private int[] cacheSizes;

	public SystemMap() {
		data = new Int2IntOpenHashMap(EngineSystem.size() + INITAL_SIZE);
		cache = new Int2ObjectOpenHashMap<SystemObject>(INITAL_SIZE);
		cacheSizes = new int[EngineSystem.size()];
	}

	public <T extends SystemObject> void cache(int system, T object) {
		synchronized (cache) {
			int cacheSize = cacheSizes[system]++;
			cache.put(id(system, size(system) + cacheSize), object);
		}
	}

	public @Nullable SystemObject getCache(int system, int index) {
		synchronized (cache) {
			return cache.getOrDefault(id(system, index), null);
		}
	}

	public void insertCache(Entity entity) {
		for (int system = 0; system < cacheSizes.length; system++) {
			int systemSize = size(system);
			int cacheSize = cacheSizes[system];
			SystemScene<?, ?> systemScene = entity.scene.getSystem(system);
			for (int i = 0; i < cacheSize; i++) {
				SystemObject object = cache.get(id(system, systemSize + i));
				add(system, systemScene.add_(entity, object));
			}
			cacheSizes[system] = 0;
		}
		cache.clear();
	}

	public int add(int system, int value) {
		int systemSize = size(system);
		setIntern(system, systemSize, value);
		setSize(system, systemSize + 1);
		size++;
		return systemSize;
	}

	public void add(int system, int... values) {
		int systemSize = size(system);
		int addSize = values.length;
		for (int i = 0; i < addSize; i++)
			setIntern(system, systemSize + i, values[i]);
		setSize(system, systemSize + addSize);
		size += addSize;
	}

	public boolean set(int system, int oldValue, int newValue) {
		int systemSize = size(system);
		for (int i = 0; i < systemSize; i++) {
			if (get(system, i) == oldValue) {
				setIntern(system, i, newValue);
				return true;
			}
		}
		return false;
	}

	public boolean remove(int system, int value) {
		int systemSize = size(system);
		for (int i = 0; i < systemSize; i++) {
			if (get(system, i) == value) {
				removeIntern(system, i);
				return true;
			}
		}
		return false;
	}

	public <T extends SystemObject> int remove(SystemScene<?, T> system, T value) {
		int systemId = system.id;
		int systemSize = size(systemId);
		for (int i = 0; i < systemSize; i++) {
			if (system.get(get(systemId, i)) == value)
				return removeIntern(systemId, i);
		}
		return -1;
	}

	public void remove(int system) {
		int systemSize = size(system);
		for (int i = 0; i < systemSize; i++)
			data.remove(id(system, i + 1));
		setSize(system, 0);
		size -= systemSize;
	}

	public void forEach(int system, IntConsumer func) {
		int systemSize = size(system);
		for (int i = 0; i < systemSize; i++)
			func.accept(get(system, i));
	}

	public void forEach(IntConsumer func) {
		int size = EngineSystem.size();
		for (int system = 0; system < size; system++) {
			int systemSize = size(system);
			for (int i = 0; i < systemSize; i++)
				func.accept(get(system, i));
		}
	}

	public void forEach(Scene scene, SystemMapConsumer func) {
		int size = EngineSystem.size();
		for (int system = 0; system < size; system++) {
			int systemSize = size(system);
			SystemScene<?, ?> systemScene = scene.getSystem(system);
			for (int i = 0; i < systemSize; i++)
				func.accept(systemScene, get(system, i));
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
				out.writeInt(get(system, i));
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

	public int get(int system, int index) {
		return data.getOrDefault(id(system, index + 1), -1);
	}

	private void setIntern(int system, int index, int value) {
		data.put(id(system, index + 1), value);
	}

	private int removeIntern(int system, int index) {
		int systemSize = size(system);
		int id = id(system, index + 1);
		int lastId = id(system, systemSize);
		int result = data.get(id);

		if (index != systemSize - 1)
			data.put(id, data.get(lastId));
		data.remove(lastId);

		setSize(system, systemSize - 1);
		size--;
		return result;
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