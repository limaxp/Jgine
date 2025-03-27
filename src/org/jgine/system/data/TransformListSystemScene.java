package org.jgine.system.data;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class TransformListSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends ListSystemScene<S, O> {

	protected Transform[] transforms;

	public TransformListSystemScene(S system, Scene scene, Class<O> clazz, int size) {
		super(system, scene, clazz, size);
		transforms = new Transform[size];
	}

	@Override
	public void relink(int index, Entity entity) {
		transforms[index] = entity.transform;
	}

	@Override
	public Entity getEntity(int index) {
		return transforms[index].getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return transforms[index];
	}
}
