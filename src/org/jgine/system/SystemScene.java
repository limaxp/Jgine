package org.jgine.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Consumer;

import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;

/**
 * The base system scene class.
 * 
 * @param <S> the {@link EngineSystem} of this {@link SystemScene}
 * @param <O>
 */
public abstract class SystemScene<S extends EngineSystem<S, O>, O extends SystemObject> {

	public final S system;
	public final String name;
	public final int id;
	public final Scene scene;

	public SystemScene(S system, Scene scene) {
		this.system = system;
		this.name = system.name;
		this.id = system.id;
		this.scene = scene;
	}

	public abstract void free();

	public abstract int add(Entity entity, O object);

	public abstract void remove(int index);

	public abstract O get(int index);

	public abstract Entity getEntity(int index);

	public abstract Transform getTransform(int index);

	public abstract void forEach(Consumer<O> func);

	public void update(UpdateTask update) {
		update.finish(system);
	}

	public void render(float dt) {
	}

	public void onRender(float dt) {
	}

	public void onInit(Entity entity, O object) {
	}

	public void onAdd(Entity entity, O object) {
	}

	public void onRemove(Entity entity, O object) {
	}

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;

	public abstract void relink(int index, Entity entity);

	public abstract int size();

	@SuppressWarnings("unchecked")
	public final void onInit_(Entity entity, SystemObject object) {
		onInit(entity, (O) object);
	}

	@SuppressWarnings("unchecked")
	public final int add_(Entity entity, SystemObject object) {
		return add(entity, (O) object);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
