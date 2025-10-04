package org.jgine.system.systems.light;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.render.light.Light;
import org.jgine.system.EngineSystem;

public class LightSystem extends EngineSystem<LightSystem, Light> {

	public LightSystem() {
		super("light");
	}

	@Override
	public LightScene createScene(Scene scene) {
		return new LightScene(this, scene);
	}

	@Override
	public Light load(Map<String, Object> data) {
		LightType<?> lightType;
		Object type = data.get("type");
		if (type != null && type instanceof String) {
			lightType = LightTypes.get((String) type);
			if (lightType == null)
				lightType = LightTypes.POINT;
		} else
			lightType = LightTypes.POINT;
		Light light = lightType.get();
		light.load(data);
		return light;
	}
}
