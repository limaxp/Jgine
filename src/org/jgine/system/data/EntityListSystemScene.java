package org.jgine.system.data;

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
	public void relink(int index, Entity entity) {
		entities[index] = entity;
	}

	@Override
	protected void resize(int size) {
		super.resize(size);
		Entity[] newArray2 = new Entity[size];
		System.arraycopy(entities, 0, newArray2, 0, getSize());
		entities = newArray2;
	}

	@Override
	public Entity getEntity(int index) {
		return entities[index];
	}

	@Override
	public Transform getTransform(int index) {
		return entities[index].transform;
	}
}
