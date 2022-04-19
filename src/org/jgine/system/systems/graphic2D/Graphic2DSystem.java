package org.jgine.system.systems.graphic2D;

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
	public Graphic2DObject load(Map<String, Object> data) {
		Graphic2DObject object = new Graphic2DObject();
		Object material = data.get("material");
		if (material != null && material instanceof String) {
			Material mat = new Material();
			mat.setTexture(ResourceManager.getTexture((String) material, mat));
			object.material = mat;
		}
		return object;
	}
}