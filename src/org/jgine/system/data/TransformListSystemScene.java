package org.jgine.system.data;

import java.util.Collection;

import org.jgine.collection.list.arrayList.FastArrayList;
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
	public int addObject(Entity entity, T2 object) {
		int index = super.addObject(entity, object);
		transforms[index] = entity.transform;
		return index;
	}

	@Override
	public T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			T2 last = objects[size];
			Transform lastTransform = transforms[size];
			objects[index] = last;
			transforms[index] = lastTransform;
			lastTransform.getEntity().setSystemId(this, last, index);
		}
		objects[size] = null;
		transforms[size] = null;
		return element;
	}

	public Collection<Transform> getTransforms() {
		return new FastArrayList<>(transforms, size);
	}

	@Override
	public Entity getEntity(int index) {
		return transforms[index].getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return transforms[index];
	}

	@Override
	protected final void resize(int size) {
		super.resize(size);
		Transform[] newArray2 = new Transform[size];
		System.arraycopy(transforms, 0, newArray2, 0, this.size);
		transforms = newArray2;
	}
}
