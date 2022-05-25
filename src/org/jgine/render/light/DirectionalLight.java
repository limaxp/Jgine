package org.jgine.render.light;

import java.util.Map;

import org.jgine.misc.math.vector.Vector3f;

public class DirectionalLight extends Light {

	private Vector3f direction = Vector3f.Z_AXIS;

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object direction = data.get("direction");
		if (direction != null && direction instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> directionMap = (Map<String, Object>) direction;
			this.direction = new Vector3f(((Number) directionMap.getOrDefault("x", 0)).floatValue(),
					((Number) directionMap.getOrDefault("y", 0)).floatValue(),
					((Number) directionMap.getOrDefault("z", 0)).floatValue());
		}
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
		hasChanged = true;
	}

	public Vector3f getDirection() {
		return direction;
	}
}
