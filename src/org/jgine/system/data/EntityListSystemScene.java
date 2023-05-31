package org.jgine.system.data;

import java.util.Collection;

import org.jgine.collection.list.arrayList.FastArrayList;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class EntityListSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends ListSystemScene<T1, T2> {

	protected Entity[] entities;

	public EntityListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene, clazz);
		entities = new Entity[ListSystemScene.INITAL_SIZE];
	}

	@Override
	public int addObject(Entity entity, T2 object) {
		int index = super.addObject(entity, object);
		entities[index] = entity;
		return index;
	}

	@Override
	public T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			T2 last = objects[size];
			Entity lastEntity = entities[size];
			objects[index] = last;
			entities[index] = lastEntity;
			lastEntity.setSystemId(this, last, index);
		}
		objects[size] = null;
		entities[size] = null;
		return element;
	}

	public Collection<Entity> getEntities() {
		return new FastArrayList<>(entities, size);
	}

	@Override
	public Entity getEntity(int index) {
		return entities[index];
	}

	public Transform getTransform(int index) {
		return entities[index].transform;
	}

	@Override
	public void relink(int index, Entity entity) {
		entities[index] = entity;
	}

	@Override
	protected void resize(int size) {
		super.resize(size);
		Entity[] newArray2 = new Entity[size];
		System.arraycopy(entities, 0, newArray2, 0, this.size);
		entities = newArray2;
	}
}
