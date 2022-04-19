package org.jgine.render.light;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.shader.Shader;

public class PointLight extends Light {

	protected final int uniform_attenuation_constant;
	protected final int uniform_attenuation_linear;
	protected final int uniform_attenuation_exponent;
	protected final int uniform_pointLight_pos;
	protected final int uniform_pointLight_range;

	public Attenuation attenuation;
	public Vector3f pos = Vector3f.NULL;
	public float range;

	public PointLight(Shader shader, int index) {
		super(shader, "pointLights[" + index + "]");
		uniform_attenuation_constant = shader.addUniform("pointLights[" + index + "].atten.constant");
		uniform_attenuation_linear = shader.addUniform("pointLights[" + index + "].atten.linear");
		uniform_attenuation_exponent = shader.addUniform("pointLights[" + index + "].atten.exponent");
		uniform_pointLight_pos = shader.addUniform("pointLights[" + index + "].pos");
		uniform_pointLight_range = shader.addUniform("pointLights[" + index + "].range");
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
		shader.setUniformf(uniform_attenuation_constant, attenuation.constant);
		shader.setUniformf(uniform_attenuation_linear, attenuation.linear);
		shader.setUniformf(uniform_attenuation_exponent, attenuation.exponent);
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setPosition(Vector3f pos) {
		this.pos = pos;
		shader.setUniform3f(uniform_pointLight_pos, pos);
	}

	public Vector3f getPosition() {
		return pos;
	}

	public void setRange(float range) {
		this.range = range;
		shader.setUniformf(uniform_pointLight_range, range);
	}

	public float getRange() {
		return range;
	}
}
