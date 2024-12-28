package org.jgine.system.data;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ListSystemScene<S extends EngineSystem<S, O>, O extends SystemObject> extends SystemScene<S, O> {

	public static final int INITAL_SIZE = 1024;

	protected Class<O> clazz;
	protected O[] objects;
	protected int size;

	@SuppressWarnings("unchecked")
	public ListSystemScene(S system, Scene scene, Class<O> clazz) {
		super(system, scene);
		this.clazz = clazz;
		objects = (O[]) Array.newInstance(clazz, INITAL_SIZE);
	}

	@Override
	public int addObject(Entity entity, O object) {
		synchronized (objects) {
			if (size == objects.length)
				ensureCapacity(size + 1);
			int index = size++;
			objects[index] = object;
			relink(index, entity);
			return index;
		}
	}

	@Override
	public O removeObject(int index) {
		synchronized (objects) {
			O element = objects[index];
			if (index != --size) {
				O last = objects[size];
				Entity lastEntity = getEntity(size);
				objects[index] = last;
				relink(index, lastEntity);
				lastEntity.setSystemId(this, size, index);
			}
			objects[size] = null;
			return element;
		}
	}

	protected void relinkEntity(int index) {
	}

	@Override
	public void forEach(Consumer<O> func) {
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				func.accept(objects[i]);
		}
	}

	@Override
	public O getObject(int index) {
		return objects[index];
	}

	@Override
	public void relink(int index, Entity entity) {
	}

	@Override
	public int getSize() {
		return size;
	}

	protected void ensureCapacity(int minCapacity) {
		int length = objects.length;
		if (minCapacity > length)
			resize(minCapacity * 2);
	}

	@SuppressWarnings("unchecked")
	protected void resize(int size) {
		O[] newArray = (O[]) Array.newInstance(clazz, size);
		System.arraycopy(objects, 0, newArray, 0, this.size);
		objects = newArray;
	}
}
