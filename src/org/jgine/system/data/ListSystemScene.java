package org.jgine.system.data;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ListSystemScene<T1 extends EngineSystem, T2 extends SystemObject> extends SystemScene<T1, T2> {

	public static final int INITAL_SIZE = 1024;

	protected Class<T2> clazz;
	protected T2[] objects;
	protected int size;

	@SuppressWarnings("unchecked")
	public ListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
		this.clazz = clazz;
		objects = (T2[]) Array.newInstance(clazz, INITAL_SIZE);
	}

	@Override
	public int addObject(Entity entity, T2 object) {
		if (size == objects.length)
			ensureCapacity(size + 1);
		int index = size++;
		objects[index] = object;
		return index;
	}

	@Override
	public T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			T2 last = objects[size];
			objects[index] = last;
			getEntity(size).setSystemId(this, last, index);
		}
		objects[size] = null;
		return element;
	}

	@Override
	public void forEach(Consumer<T2> func) {
		for (int i = 0; i < size; i++)
			func.accept(objects[i]);
	}

	@Override
	public T2 getObject(int index) {
		return objects[index];
	}

	@Override
	public void relink(int index, Entity entity) {
	}

	protected void ensureCapacity(int minCapacity) {
		int length = objects.length;
		if (minCapacity > length)
			resize(minCapacity * 2);
	}

	@SuppressWarnings("unchecked")
	protected void resize(int size) {
		T2[] newArray = (T2[]) Array.newInstance(clazz, size);
		System.arraycopy(objects, 0, newArray, 0, this.size);
		objects = newArray;
	}
}
