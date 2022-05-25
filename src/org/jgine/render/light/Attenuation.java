package org.jgine.render.light;

public class Attenuation {

	public static final Attenuation DEFAULT = new Attenuation(0, 0, 1);

	public final float constant;
	public final float linear;
	public final float exponent;

	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
	}
}
