package org.jgine.system.data;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class TransformListSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends ListSystemScene<T1, T2> {

	protected Transform[] transforms;

	public TransformListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene, clazz);
		transforms = new Transform[ListSystemScene.INITAL_SIZE];
	}

	@Override
	public void relink(int index, Entity entity) {
		transforms[index] = entity.transform;
	}

	@Override
	protected final void resize(int size) {
		super.resize(size);
		Transform[] newArray2 = new Transform[size];
		System.arraycopy(transforms, 0, newArray2, 0, getSize());
		transforms = newArray2;
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
