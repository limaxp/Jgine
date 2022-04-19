package org.jgine.system.systems.transform;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.EngineSystem;

public class TransformSystem extends EngineSystem {

	@Override
	public TransformScene createScene(Scene scene) {
		return new TransformScene(this, scene);
	}

	@Override
	public Transform load(Map<String, Object> data) {
		Transform object = new Transform();
		Object position = data.get("position");
		if (position != null && position instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> positionMap = (Map<String, Object>) position;
			object.setPositionNoUpdate(new Vector3f(((Number) positionMap.getOrDefault("x", 0)).floatValue(),
					((Number) positionMap.getOrDefault(
							"y", 0)).floatValue(), ((Number) positionMap.getOrDefault("z", 0)).floatValue()));
		}
		Object rotation = data.get("rotation");
		if (rotation != null && rotation instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rotationMap = (Map<String, Object>) rotation;
			object.setRotation(new Vector3f(((Number) rotationMap.getOrDefault("x", 0)).floatValue(),
					((Number) rotationMap.getOrDefault(
							"y", 0)).floatValue(), ((Number) rotationMap.getOrDefault("z", 0)).floatValue()));
		}
		Object scale = data.get("scale");
		if (scale != null && scale instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> scaleMap = (Map<String, Object>) scale;
			object.setScale(new Vector3f(((Number) scaleMap.getOrDefault("x", 0)).floatValue(), ((Number) scaleMap
					.getOrDefault("y", 0)).floatValue(),
					((Number) scaleMap.getOrDefault("z", 0)).floatValue()));
		}
		return object;
	}
}
