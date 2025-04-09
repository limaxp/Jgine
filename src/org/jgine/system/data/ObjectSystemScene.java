package org.jgine.system.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ObjectSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends SystemScene<S, O> {

	private final O[] objects;
	private int size;

	@SuppressWarnings("unchecked")
	public ObjectSystemScene(S system, Scene scene, Class<O> clazz, int size) {
		super(system, scene);
		objects = (O[]) Array.newInstance(clazz, size);
	}

	@Override
	public int add(Entity entity, O object) {
		if (size == objects.length)
			return -1;
		int index = size++;
		objects[index] = object;
		relink(index, entity);
		return index;
	}

	@Override
	public void remove(int index) {
		if (index != --size) {
			objects[index] = objects[size];
			Entity lastEntity = getEntity(size);
			relink(index, lastEntity);
			lastEntity.setSystemId(this, size, index);
		}
		objects[size] = null;
	}

	@Override
	public void forEach(Consumer<O> func) {
		for (int i = 0; i < size; i++)
			func.accept(objects[i]);
	}

	@Override
	public O get(int index) {
		return objects[index];
	}

	@Override
	public void relink(int index, Entity entity) {
	}

	@Override
	public int size() {
		return size;
	}

	protected void swap(int index1, int index2) {
		O tmp = objects[index1];
		objects[index1] = objects[index2];
		objects[index2] = tmp;
		Entity first = getEntity(index1);
		Entity second = getEntity(index2);
		relink(index1, second);
		relink(index2, first);
		first.setSystemId(this, index1, index2);
		second.setSystemId(this, index2, index1);
	}

	@Override
	public final void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			saveData(objects[i], out);
	}

	@Override
	public final void load(DataInput in) throws IOException {
		size = in.readInt();
		for (int i = 0; i < size; i++)
			objects[i] = loadData(in);
	}

	protected abstract void saveData(O object, DataOutput out) throws IOException;

	protected abstract O loadData(DataInput in) throws IOException;
}
