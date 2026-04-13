package jgine.system.graphic;

import java.util.Map;

import jgine.core.Scene;
import jgine.render.material.Material;
import jgine.system.EngineSystem;

public class Graphic2DSystem extends EngineSystem<Graphic2DSystem, Material> {

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