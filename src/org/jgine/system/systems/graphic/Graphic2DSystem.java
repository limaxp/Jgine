package org.jgine.system.systems.graphic;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.EngineSystem;

public class Graphic2DSystem extends EngineSystem {

	@Override
	public Graphic2DScene createScene(Scene scene) {
		return new Graphic2DScene(this, scene);
	}

	@Override
	public Material load(Map<String, Object> data) {
		Material object = new Material();
		Object texture = data.get("texture");
		if (texture != null && texture instanceof String) {
			object.setTexture(ResourceManager.getTexture((String) texture, object));
		}
		return object;
	}
}