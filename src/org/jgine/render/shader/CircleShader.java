package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;

public class CircleShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_color = addUniform("baseColor");

	public CircleShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		setUniformi(uniform_uTexture, 0);
		setUniformColor(uniform_color, material.color);
	}
}
