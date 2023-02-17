package org.jgine.system.data;

import java.lang.reflect.Array;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ListSystemScene<T1 extends EngineSystem, T2 extends SystemObject> extends SystemScene<T1, T2> {

	public static final int GROW_SIZE = 1000;

	protected Class<T2> clazz;
	protected T2[] objects;
	protected int size;

	@SuppressWarnings("unchecked")
	public ListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
		this.clazz = clazz;
		objects = (T2[]) Array.newInstance(clazz, GROW_SIZE);
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		if (size == objects.length)
			ensureCapacity(size + 1);
		objects[size++] = object;
		return object;
	}

	@Override
	@Nullable
	public T2 removeObject(T2 object) {
		int index = indexOf(object);
		if (index != -1)
			removeObject(index);
		return object;
	}

	protected T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size)
			objects[index] = objects[size];
		objects[size] = null;
		return element;
	}

	@Override
	public Collection<T2> getObjects() {
		return new FastArrayList<>(objects, size);
	}

	@Override
	public T2 getObject(int index) {
		return objects[index];
	}

	public int indexOf(T2 object) {
		for (int i = 0; i < size; i++)
			if (objects[i] == object)
				return i;
		return -1;
	}

	public int lastIndexOf(T2 object) {
		for (int i = size; i >= 0; i--)
			if (objects[i] == object)
				return i;
		return -1;
	}

	protected void ensureCapacity(int minCapacity) {
		int length = objects.length;
		if (minCapacity > length)
			resize(minCapacity + GROW_SIZE);
	}

	@SuppressWarnings("unchecked")
	protected void resize(int size) {
		T2[] newArray = (T2[]) Array.newInstance(clazz, size);
		System.arraycopy(objects, 0, newArray, 0, this.size);
		objects = newArray;
	}
}
