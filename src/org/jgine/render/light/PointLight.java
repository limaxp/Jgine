package org.jgine.render.light;

import java.util.Map;

import org.jgine.misc.math.vector.Vector3f;

public class PointLight extends Light {

	private Attenuation attenuation = Attenuation.DEFAULT;
	private Vector3f pos = Vector3f.NULL;
	private float range = 1;

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object attenuation = data.get("attenuation");
		if (attenuation != null && attenuation instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> attenuationMap = (Map<String, Object>) attenuation;
			this.attenuation = new Attenuation(((Number) attenuationMap.getOrDefault("constant", 0)).floatValue(),
					((Number) attenuationMap.getOrDefault("linear", 0)).floatValue(),
					((Number) attenuationMap.getOrDefault("exponent", 1)).floatValue());
		}

		Object pos = data.get("pos");
		if (pos != null && pos instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> posMap = (Map<String, Object>) pos;
			this.pos = new Vector3f(((Number) posMap.getOrDefault("x", 0)).floatValue(),
					((Number) posMap.getOrDefault("y", 0)).floatValue(),
					((Number) posMap.getOrDefault("z", 0)).floatValue());
		}
		Object range = data.get("range");
		if (range != null && range instanceof Number)
			this.range = ((Number) range).floatValue();
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
		hasChanged = true;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setPosition(Vector3f pos) {
		this.pos = pos;
		hasChanged = true;
	}

	public Vector3f getPosition() {
		return pos;
	}

	public void setRange(float range) {
		this.range = range;
		hasChanged = true;
	}

	public float getRange() {
		return range;
	}
}
