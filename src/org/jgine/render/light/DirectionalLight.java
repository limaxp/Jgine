package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.shader.Shader;

public class DirectionalLight extends Light {

	protected final int uniform_directionalLight_direction;

	public Vector3f direction = Vector3f.NULL;

	public DirectionalLight(Shader shader) {
		super(shader, "directionalLight");
		uniform_directionalLight_direction = shader.addUniform("directionalLight.direction");
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
		shader.setUniform3f(uniform_directionalLight_direction, direction);
	}

	public Vector3f getDirection() {
		return direction;
	}
}
