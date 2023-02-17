package org.jgine.system.data;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public abstract class TransformListSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends ListSystemScene<T1, T2> {

	protected Transform[] transforms;

	public TransformListSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene, clazz);
		transforms = new Transform[ListSystemScene.GROW_SIZE];
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		super.addObject(entity, object);
		transforms[size - 1] = entity.transform;
		return object;
	}

	@Override
	@Nullable
	protected T2 removeObject(int index) {
		T2 element = objects[index];
		if (index != --size) {
			objects[index] = objects[size];
			transforms[index] = transforms[size];
		}
		objects[size] = null;
		transforms[size] = null;
		return element;
	}

	public Collection<Transform> getTransforms() {
		return new FastArrayList<>(transforms, size);
	}

	public Transform getTransform(int index) {
		return transforms[index];
	}

	public int indexOf(Transform transform) {
		for (int i = 0; i < size; i++)
			if (transforms[i] == transform)
				return i;
		return -1;
	}

	public int lastIndexOf(Transform transform) {
		for (int i = size; i >= 0; i--)
			if (transforms[i] == transform)
				return i;
		return -1;
	}

	@Override
	protected final void resize(int size) {
		super.resize(size);
		Transform[] newArray2 = new Transform[size];
		System.arraycopy(transforms, 0, newArray2, 0, this.size);
		transforms = newArray2;
	}
}
