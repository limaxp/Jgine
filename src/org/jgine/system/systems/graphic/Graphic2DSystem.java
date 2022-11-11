package org.jgine.system.systems.graphic;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.EngineSystem;

public class Graphic2DSystem extends EngineSystem {

	public Graphic2DSystem() {
		super("graphic2d");
	}

	@Override
	public Graphic2DScene createScene(Scene scene) {
		return new Graphic2DScene(this, scene);
	}

	@Override
	public Material load(Map<String, Object> data) {
		Material material = new Material();
		material.load(data);
		return material;
	}
}