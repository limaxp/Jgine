package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public abstract class Light implements SystemObject {

	protected boolean hasChanged = true;
	private Vector3f color = Vector3f.FULL;
	private float intensity;

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
