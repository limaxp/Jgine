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

public abstract class EntityListSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends SystemScene<T1, T2> {

	protected Class<T2> clazz;
	protected T2[] objects;
	protected Entity[] entities;
	protected int size;

	@SuppressWarnings("unchecked")
	public EntityListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
		this.clazz = clazz;
		objects = (T2[]) Array.newInstance(clazz, ListSystemScene.GROW_SIZE);
		entities = new Entity[ListSystemScene.GROW_SIZE];
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		if (size == objects.length)
			ensureCapacity(size + 1);
		objects[size] = object;
		entities[size++] = entity;
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

	private T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			objects[index] = objects[size];
			entities[index] = entities[size];
		}
		objects[size] = null;
		entities[size] = null;
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

	public Collection<Entity> getEntities() {
		return new FastArrayList<>(entities, size);
	}

	public Entity getEntity(int index) {
		return entities[index];
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

	public int indexOf(Entity entity) {
		for (int i = 0; i < size; i++)
			if (entities[i] == entity)
				return i;
		return -1;
	}

	public int lastIndexOf(Entity entity) {
		for (int i = size; i >= 0; i--)
			if (entities[i] == entity)
				return i;
		return -1;
	}

	private final void ensureCapacity(int minCapacity) {
		int length = objects.length;
		if (minCapacity > length)
			resize(minCapacity + ListSystemScene.GROW_SIZE);
	}

	@SuppressWarnings("unchecked")
	private final void resize(int size) {
		T2[] newArray = (T2[]) Array.newInstance(clazz, size);
		System.arraycopy(objects, 0, newArray, 0, this.size);
		objects = newArray;
		Entity[] newArray2 = new Entity[size];
		System.arraycopy(entities, 0, newArray2, 0, this.size);
		entities = newArray2;
	}
}
