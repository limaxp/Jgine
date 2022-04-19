package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;

public class TextureShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureOffsets = addUniform("textureOffsets");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_color = addUniform("color");

	public TextureShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		Texture texture = material.getTexture();
		setUniformi(uniform_uTexture, 0);
		int col = material.getTexturePosition() % texture.getColums();
		int row = material.getTexturePosition() / texture.getColums();
		float textXOffset = (float) col / texture.getColums();
		float textYOffset = (float) row / texture.getRows();
		setUniform2f(uniform_textureOffsets, textXOffset, textYOffset);
		setUniformi(uniform_textureColums, texture.getColums());
		setUniformi(uniform_textureRows, texture.getRows());
		setUniform4f(uniform_color, material.color);
	}
}
