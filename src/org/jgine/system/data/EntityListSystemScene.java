package org.jgine.system.data;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class EntityListSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends ListSystemScene<S, O> {

	protected Entity[] entities;

	public EntityListSystemScene(S system, Scene scene, Class<O> clazz) {
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
