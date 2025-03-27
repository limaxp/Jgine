package org.jgine.system.data;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public abstract class ListSystemScene<S extends EngineSystem<S, O>, O extends SystemObject> extends SystemScene<S, O> {

	protected O[] objects;
	protected int size;

	@SuppressWarnings("unchecked")
	public ListSystemScene(S system, Scene scene, Class<O> clazz, int size) {
		super(system, scene);
		objects = (O[]) Array.newInstance(clazz, size);
	}

	@Override
	public int addObject(Entity entity, O object) {
		if (size == objects.length)
			return -1;
		int index = size++;
		objects[index] = object;
		relink(index, entity);
		return index;
	}

	@Override
	public O removeObject(int index) {
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

	@Override
	public void forEach(Consumer<O> func) {
		for (int i = 0; i < size; i++)
			func.accept(objects[i]);
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
}
