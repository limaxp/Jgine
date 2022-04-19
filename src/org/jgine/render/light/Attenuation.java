package org.jgine.render.light;

public class Attenuation {

	public final float constant;
	public final float linear;
	public final float exponent;

	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
	}
}
