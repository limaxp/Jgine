package jgine.system.transform;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;

public class TransformSystem extends EngineSystem<TransformSystem, Transform> {

	public TransformSystem() {
		super("transform");
	}

	@Override
	public TransformScene createScene(Scene scene) {
		return new TransformScene(this, scene);
	}

	@Override
	public Transform load(Map<String, Object> data) {
		Transform object = new Transform();
		object.load(data);
		return object;
	}
}