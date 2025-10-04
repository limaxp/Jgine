package org.jgine.system.systems.particle;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

public class ParticleSystem extends EngineSystem<ParticleSystem, Particle> {

	public ParticleSystem() {
		super("particle");
	}

	@Override
	public ParticleScene createScene(Scene scene) {
		return new ParticleScene(this, scene);
	}

	@Override
	public Particle load(Map<String, Object> data) {
		Particle object = new Particle();
		object.load(data);
		return object;
	}
}
