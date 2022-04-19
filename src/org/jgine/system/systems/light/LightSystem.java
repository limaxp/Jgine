package org.jgine.system.systems.light;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.render.light.Light;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class LightSystem extends EngineSystem {

	@Override
	public SystemScene<?, ?> createScene(Scene scene) {
		return new LightScene(this, scene);
	}

	@Override
	public Light load(Map<String, Object> data) {
		// Light object = new Light();
		return null;
	}
}
