package org.jgine.render.shader;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.light.Attenuation;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.PointLight;

public class PhongShader extends TextureShader {

	public static final int MAX_POINT_LIGHTS = 4;

	public final int uniform_transform = addUniform("transform");
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
		super.setMaterial(material);
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