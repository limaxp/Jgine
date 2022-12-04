package org.jgine.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;

public abstract class SystemScene<T1 extends EngineSystem, T2 extends SystemObject> {

	public final T1 system;
	public final Scene scene;

	public SystemScene(T1 system, Scene scene) {
		this.system = system;
		this.scene = scene;
	}

	public abstract void free();

	public abstract void initObject(Entity entity, T2 object);

	public abstract T2 addObject(Entity entity, T2 object);

	@Nullable
	public abstract T2 removeObject(T2 object);

	@SuppressWarnings("unchecked")
	public final T2 removeObject_(SystemObject object) {
		return removeObject((T2) object);
	}

	public abstract Collection<T2> getObjects();

	public abstract T2 getObject(int index);

	public abstract void update(float dt);

	public abstract void render();

	public abstract T2 load(DataInput in) throws IOException;

	public abstract void save(T2 object, DataOutput out) throws IOException;

	@SuppressWarnings("unchecked")
	public void save_(SystemObject object, DataOutput out) throws IOException {
		save((T2) object, out);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
