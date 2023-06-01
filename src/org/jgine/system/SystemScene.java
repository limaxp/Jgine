package org.jgine.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;

/**
 * The base system scene class.
 * 
 * @param <T1> the {@link EngineSystem} of this {@link SystemScene}
 * @param <T2>
 */
public abstract class SystemScene<T1 extends EngineSystem, T2 extends SystemObject> {

	public final T1 system;
	public final String name;
	public final int id;
	public final Scene scene;

	public SystemScene(T1 system, Scene scene) {
		this.system = system;
		this.name = system.name;
		this.id = system.id;
		this.scene = scene;
	}

	public abstract void free();

	public abstract void initObject(Entity entity, T2 object);

	public abstract int addObject(Entity entity, T2 object);

	public abstract T2 removeObject(int index);

	public abstract Collection<T2> getObjects();

	public abstract T2 getObject(int index);

	public abstract Entity getEntity(int index);

	public abstract Transform getTransform(int index);

	public abstract void update(float dt);

	public abstract void render(float dt);

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;

	public abstract void relink(int index, Entity entity);

	@SuppressWarnings("unchecked")
	public final void initObject_(Entity entity, SystemObject object) {
		initObject(entity, (T2) object);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
