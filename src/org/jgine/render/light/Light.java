package org.jgine.render.light;

import java.util.Map;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public abstract class Light implements SystemObject {

	protected boolean hasChanged = true;
	private Vector3f color = Vector3f.FULL;
	private float intensity = 1.0f;

	public void load(Map<String, Object> data) {
		Object color = data.get("color");
		if (color != null && color instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> colorMap = (Map<String, Object>) color;
			this.color = new Vector3f(((Number) colorMap.getOrDefault("r", 0)).floatValue(),
					((Number) colorMap.getOrDefault("g", 0)).floatValue(),
					((Number) colorMap.getOrDefault("b", 0)).floatValue());
		}
		Object intensity = data.get("intensity");
		if (intensity != null && intensity instanceof Number)
			this.intensity = ((Number) intensity).floatValue();
	}

	public void setColor(Vector3f color) {
		this.color = color;
		hasChanged = true;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		hasChanged = true;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setChanged() {
		hasChanged = true;
	}

	public boolean checkChanged() {
		boolean result = hasChanged;
		hasChanged = false;
		return result;
	}
}
