package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;

public class TextureShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureOffsets = addUniform("textureOffsets");
	public final int uniform_baseColor = addUniform("baseColor");

	public TextureShader(String name) {
		super(name);
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
		setUniformColor(uniform_baseColor, material.color);
	}
}
