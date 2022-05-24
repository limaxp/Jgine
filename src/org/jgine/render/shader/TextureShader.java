package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.ITexture;
import org.jgine.render.graphic.material.Material;

public class TextureShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureOffsets = addUniform("textureOffsets");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_baseColor = addUniform("baseColor");

	public TextureShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		ITexture texture = material.getTexture();
		setUniformi(uniform_uTexture, 0);
		int colums = texture.getColums();
		int rows = texture.getRows();
		int colum = material.getTexturePosition() % colums;
		int row = material.getTexturePosition() / colums;
		float textXOffset = (float) colum / colums;
		float textYOffset = (float) row / rows;
		setUniform2f(uniform_textureOffsets, textXOffset, textYOffset);
		setUniformi(uniform_textureColums, colums);
		setUniformi(uniform_textureRows, rows);
		setUniform4f(uniform_baseColor, material.color);
	}
}
