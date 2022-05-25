package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;

public class DirectionalLight extends Light {

	private Vector3f direction = Vector3f.Z_AXIS;

	public void setDirection(Vector3f direction) {
		this.direction = direction;
		hasChanged = true;
	}

	public Vector3f getDirection() {
		return direction;
	}
}
