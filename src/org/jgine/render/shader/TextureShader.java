package org.jgine.render.shader;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Material;
import org.jgine.utils.math.Matrix;

public class TextureShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureOffsets = addUniform("textureOffsets");
	public final int uniform_baseColor = addUniform("baseColor");

	public TextureShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super(vertex, geometry, fragment);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		setUniformi(uniform_uTexture, 0);
		setUniform4f(uniform_textureOffsets, material.getTextureX(), material.getTextureY(), material.getTextureWidth(),
				material.getTextureHeight());
		setUniformRGBA(uniform_baseColor, material.color);
	}
}
