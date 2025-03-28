package org.jgine.system.data;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ObjectSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends SystemScene<S, O> {

	protected O[] objects;
	protected int size;

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
	public int getSize() {
		return size;
	}
}
