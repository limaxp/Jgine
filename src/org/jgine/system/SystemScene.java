package org.jgine.system;

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

	public abstract void update();

	public abstract void render();

	@SuppressWarnings("unchecked")
	public final void parentUpdate_(Entity entity, SystemObject object) {
		parentUpdate(entity, (T2) object);
	}

	public void parentUpdate(Entity entity, T2 object) {
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
