package org.jgine.system.systems.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.system.data.ObjectSystemScene;

public class ParticleScene extends ObjectSystemScene<ParticleSystem, Particle> {

	public ParticleScene(ParticleSystem system, Scene scene) {
		super(system, scene, Particle.class, 10000);
	}

	@Override
	public void free() {
		forEach(Particle::close);
	}

	@Override
	public void init(Entity entity, Particle object) {
		object.transform = entity.transform;
	}

	@Override
	public void remove(int index) {
		get(index).close();
		super.remove(index);
	}

	@Override
	public void onRender(float dt) {
		Renderer.PARTICLE_CALC_SHADER.bind();
		for (int i = 0; i < size(); i++) {
			Particle object = get(i);
			Renderer.PARTICLE_CALC_SHADER.setParticle(object, dt);
			object.getMesh().update();
		}
		Renderer.PARTICLE_CALC_SHADER.unbind();
	}

	@Override
	public void render(float dt) {
		Renderer.enableDepthTest();
		Renderer.setShader(Renderer.PARTICLE_DRAW_SHADER);
		for (int i = 0; i < size(); i++) {
			Particle object = get(i);
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
		return get(index).transform;
	}

	@Override
	protected void saveData(Particle object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected Particle loadData(DataInput in) throws IOException {
		Particle object = new Particle();
		object.load(in);
		return object;
	}
}
