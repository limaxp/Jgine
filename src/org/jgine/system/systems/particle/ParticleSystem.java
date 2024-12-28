package org.jgine.system.systems.particle;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

public class ParticleSystem extends EngineSystem<ParticleSystem, ParticleObject> {

	public ParticleSystem() {
		super("particle");
	}

	@Override
	public ParticleScene createScene(Scene scene) {
		return new ParticleScene(this, scene);
	}

	@Override
	public ParticleObject load(Map<String, Object> data) {
		ParticleObject object = new ParticleObject();
		object.load(data);
		return object;
	}
}
