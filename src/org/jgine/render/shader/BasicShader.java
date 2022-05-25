package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;

public class BasicShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");

	public BasicShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
	}
}
