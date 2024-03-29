package org.jgine.render.shader;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Material;
import org.jgine.utils.math.Matrix;

public class BasicShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");

	public BasicShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super(vertex, geometry, fragment);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
	}
}
