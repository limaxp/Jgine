package org.jgine.system.data;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.list.arrayList.FastArrayList;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class EntityListSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends ListSystemScene<T1, T2> {

	protected Entity[] entities;

	public EntityListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene, clazz);
		entities = new Entity[ListSystemScene.GROW_SIZE];
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		super.addObject(entity, object);
		entities[size - 1] = entity;
		return object;
	}

	@Override
	@Nullable
	protected T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			objects[index] = objects[size];
			entities[index] = entities[size];
		}
		objects[size] = null;
		entities[size] = null;
		return element;
	}

	public Collection<Entity> getEntities() {
		return new FastArrayList<>(entities, size);
	}

	public Entity getEntity(int index) {
		return entities[index];
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

	@Override
	protected void resize(int size) {
		super.resize(size);
		Entity[] newArray2 = new Entity[size];
		System.arraycopy(entities, 0, newArray2, 0, this.size);
		entities = newArray2;
	}
}
