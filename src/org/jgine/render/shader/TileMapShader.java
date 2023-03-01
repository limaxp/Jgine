package org.jgine.render.shader;

import org.jgine.render.material.ITexture;
import org.jgine.render.material.Material;
import org.jgine.utils.math.Matrix;

public class TileMapShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_color = addUniform("baseColor");

	public TileMapShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		ITexture texture = material.getTexture();
		setUniformi(uniform_uTexture, 0);
		setUniformi(uniform_textureColums, texture.getColums());
		setUniformi(uniform_textureRows, texture.getRows());
		setUniformColor(uniform_color, material.color);
	}
}