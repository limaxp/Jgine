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
		for (int i = 0; i < size; i++)
			objects[i].close();
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
	public void render() {
		Renderer.PARTICLE_CALC_SHADER.bind();
		for (int i = 0; i < size; i++)
			objects[i].update(objects[i].transform);

		for (int i = 0; i < size; i++) {
			ParticleObject object = objects[i];
			Renderer.render(object.transform.getMatrix(), object, Renderer.PARTICLE_DRAW_SHADER, object.material);
		}
	}

	@Override
	public ParticleObject load(DataInput in) throws IOException {
		ParticleObject object = new ParticleObject();
		return object;
	}

	@Override
	public void save(ParticleObject object, DataOutput out) throws IOException {
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return objects[index].transform;
	}
}
