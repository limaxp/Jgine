package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.shader.Shader;
import org.jgine.system.SystemObject;

public abstract class Light implements SystemObject {

	protected final int uniform_baseLight_color;
	protected final int uniform_baseLight_intensity;

	public final Shader shader;
	private Vector3f color = Vector3f.NULL;
	private float intensity;

	public Light(Shader shader, String name) {
		this.shader = shader;
		uniform_baseLight_color = shader.addUniform(name + ".base.color");
		uniform_baseLight_intensity = shader.addUniform(name + ".base.intensity");
	}

	public void setColor(Vector3f color) {
		this.color = color;
		shader.setUniform3f(uniform_baseLight_color, color);
	}

	public Vector3f getColor() {
		return color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		shader.setUniformf(uniform_baseLight_intensity, intensity);
	}

	public float getIntensity() {
		return intensity;
	}
}
