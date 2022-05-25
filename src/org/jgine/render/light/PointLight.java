package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;

public class PointLight extends Light {

	private Attenuation attenuation = Attenuation.DEFAULT;
	private Vector3f pos = Vector3f.NULL;
	private float range = 1;

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
