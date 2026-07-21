package jgine.system.light;

import java.util.Map;

import jgine.core.Registry;
import jgine.core.Scene;
import jgine.render.light.Light;
import jgine.system.EngineSystem;

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
			lightType = Registry.LIGHT.get((String) type);
			if (lightType == null)
				lightType = LightType.POINT;
		} else
			lightType = LightType.POINT;
		Light light = lightType.get();
		light.load(data);
		return light;
	}
}
