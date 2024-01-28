package org.jgine.system.systems.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.system.data.ListSystemScene;

public class ParticleScene extends ListSystemScene<ParticleSystem, ParticleObject> {

	public ParticleScene(ParticleSystem system, Scene scene) {
		super(system, scene, ParticleObject.class);
	}

	@Override
	public void free() {
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				objects[i].close();
		}
	}

	@Override
	public void initObject(Entity entity, ParticleObject object) {
		object.transform = entity.transform;
	}

	@Override
	public ParticleObject removeObject(int index) {
		ParticleObject object = super.removeObject(index);
		object.close();
		return object;
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(float dt) {
		Renderer.PARTICLE_CALC_SHADER.bind();
		for (int i = 0; i < size; i++) {
			Renderer.PARTICLE_CALC_SHADER.setParticle(objects[i], dt);
			objects[i].update();
		}
		Renderer.PARTICLE_CALC_SHADER.unbind();

		for (int i = 0; i < size; i++) {
			ParticleObject object = objects[i];
			Renderer.render(object.transform.getMatrix(), object, Renderer.PARTICLE_DRAW_SHADER, object.material);
		}
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return objects[index].transform;
	}

	@Override
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			@SuppressWarnings("resource")
			ParticleObject object = new ParticleObject();
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			objects[i].save(out);
	}
}
