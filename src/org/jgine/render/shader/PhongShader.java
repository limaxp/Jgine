package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.light.Attenuation;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.PointLight;

public class PhongShader extends Shader {

	public static final int MAX_POINT_LIGHTS = 4;

	public final int uniform_transform = addUniform("transform");
	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureOffsets = addUniform("textureOffsets");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_baseColor = addUniform("baseColor");
	public final int uniform_ambientLight = addUniform("ambientLight");
	public final int uniform_camPos = addUniform("camPos");
	public final int uniform_specularIntensity = addUniform("specularIntensity");
	public final int uniform_specularPower = addUniform("specularPower");

	private Vector3f ambientLight;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights = new PointLight[MAX_POINT_LIGHTS];

	public PhongShader(String name) {
		super(name);
		bind();
		setAmbientLight(new Vector3f(0.04f));

		directionalLight = new DirectionalLight(this);
		directionalLight.setColor(new Vector3f(1f, 1f, 1f));
		directionalLight.setIntensity(0.8f);
		directionalLight.setDirection(new Vector3f(1f, 1f, -1f));

		for (int i = 0; i < MAX_POINT_LIGHTS; i++)
			pointLights[i] = new PointLight(this, i);

		PointLight first = pointLights[0];
		first.setColor(new Vector3f(1, 0.5f, 0));
		first.setIntensity(0.8f);
		first.setAttenuation(new Attenuation(0, 0, 1));
		first.setPosition(new Vector3f(-3, 0, 4));
		first.setRange(6);

		PointLight second = pointLights[1];
		second.setColor(new Vector3f(0, 0.5f, 1));
		second.setIntensity(0.8f);
		second.setAttenuation(new Attenuation(0, 0, 1));
		second.setPosition(new Vector3f(3, 0, 4));
		second.setRange(6);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix(uniform_transform, matrix);
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
		setUniform4f(uniform_baseColor, material.color);
		setUniformf(uniform_specularIntensity, material.specularIntesity);
		setUniformf(uniform_specularPower, material.specularPower);
	}

	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
		setUniform3f(uniform_ambientLight, this.ambientLight);
	}

	public void setAmbientLight(float r, float g, float b) {
		this.ambientLight = new Vector3f(r, g, b);
		setUniform3f(uniform_ambientLight, ambientLight);
	}

	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public PointLight getPointLight(int index) {
		return pointLights[index];
	}
}