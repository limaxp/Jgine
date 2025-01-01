package org.jgine.system.systems.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.system.data.ListSystemScene;

public class ParticleScene extends ListSystemScene<ParticleSystem, Particle> {

	public ParticleScene(ParticleSystem system, Scene scene) {
		super(system, scene, Particle.class);
	}

	@Override
	public void free() {
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				objects[i].close();
		}
	}

	@Override
	public void initObject(Entity entity, Particle object) {
		object.transform = entity.transform;
	}

	@Override
	public Particle removeObject(int index) {
		Particle object = super.removeObject(index);
		object.close();
		return object;
	}

	@Override
	public void render(float dt) {
		Renderer.PARTICLE_CALC_SHADER.bind();
		for (int i = 0; i < size; i++) {
			Renderer.PARTICLE_CALC_SHADER.setParticle(objects[i], dt);
			objects[i].getMesh().update();
		}
		Renderer.PARTICLE_CALC_SHADER.unbind();

		Renderer.enableDepthTest();
		Renderer.setShader(Renderer.PARTICLE_DRAW_SHADER);
		for (int i = 0; i < size; i++) {
			Particle object = objects[i];
			Renderer.render(object.transform.getMatrix(), object.getMesh(), object.material);
		}
		Renderer.disableDepthTest();
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
			Particle object = new Particle();
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
