package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;

public class TileMapShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_color = addUniform("color");

	public TileMapShader(String name) {
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
		setUniformi(uniform_textureColums, texture.getColums());
		setUniformi(uniform_textureRows, texture.getRows());
		setUniform4f(uniform_color, material.color);
	}
}