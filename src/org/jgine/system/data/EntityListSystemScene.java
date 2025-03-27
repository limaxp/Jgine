package org.jgine.system.data;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class EntityListSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends ListSystemScene<S, O> {

	protected Entity[] entities;

	public EntityListSystemScene(S system, Scene scene, Class<O> clazz, int size) {
		super(system, scene, clazz, size);
		entities = new Entity[size];
	}

	@Override
	public void relink(int index, Entity entity) {
		entities[index] = entity;
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
