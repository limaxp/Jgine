package org.jgine.render.shader;

import java.util.Collections;
import java.util.List;

import org.jgine.core.manager.SystemManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.Color;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.PointLight;
import org.jgine.system.systems.camera.CameraSystem;

public class PhongShader extends TextureShader {

	public static final int MAX_POINT_LIGHTS = 8;

	public final int uniform_transform = addUniform("transform");
	public final int uniform_ambientLight = addUniform("ambientLight");
	public final int uniform_camPos = addUniform("camPos");
	public final int uniform_specularIntensity = addUniform("specularIntensity");
	public final int uniform_specularPower = addUniform("specularPower");
	public final int uniform_directionalLight_color = addUniform("directionalLight.base.color");
	public final int uniform_directionalLight_intensity = addUniform("directionalLight.base.intensity");
	public final int uniform_directionalLight_direction = addUniform("directionalLight.direction");
	public final int uniform_pointLightSize = addUniform("pointLightSize");
	protected final int uniforms_pointLights[][] = addUniforms("pointLights", MAX_POINT_LIGHTS, new String[] {
			"base.color", "base.intensity", "atten.constant", "atten.linear", "atten.exponent", "pos", "range" });

	private Vector3f ambientLight;
	private boolean changedAmbientLight = false;
	private DirectionalLight directionalLight;
	private List<PointLight> pointLights;

	public PhongShader(String name) {
		super(name);
		pointLights = new UnorderedArrayList<PointLight>(MAX_POINT_LIGHTS);

		setAmbientLight(new Vector3f(0.0f));

		directionalLight = new DirectionalLight();
		directionalLight.setIntensity(0.8f);
		directionalLight.setDirection(new Vector3f(1f, 1f, -1f));
	}

	@Override
	public void bind() {
		super.bind();

		setUniform3f(uniform_camPos,
				((CameraSystem) SystemManager.get("camera")).getMainCamera().getTransform().getPosition());

		if (changedAmbientLight) {
			changedAmbientLight = false;
			setUniform3f(uniform_ambientLight, ambientLight);
		}

		if (directionalLight.checkChanged()) {
			// TODO color?
			Vector4f color = Color.toVector(directionalLight.getColor());
			setUniform3f(uniform_directionalLight_color, color.x, color.y, color.z);
			setUniformf(uniform_directionalLight_intensity, directionalLight.getIntensity());
			setUniform3f(uniform_directionalLight_direction, directionalLight.getDirection());
		}

		int pointLightSize = FastMath.min(pointLights.size(), MAX_POINT_LIGHTS);
		setUniformi(uniform_pointLightSize, pointLightSize);
		for (int i = 0; i < pointLightSize; i++) {
			PointLight pointLight = pointLights.get(i);
			if (pointLight.checkChanged()) {
				int[] pointLightUniforms = uniforms_pointLights[i];
				Vector4f color = Color.toVector(pointLight.getColor());
				setUniform3f(pointLightUniforms[0], color.x, color.y, color.z);
				setUniformf(pointLightUniforms[1], pointLight.getIntensity());
				setUniformf(pointLightUniforms[2], pointLight.getAttenuation().constant);
				setUniformf(pointLightUniforms[3], pointLight.getAttenuation().linear);
				setUniformf(pointLightUniforms[4], pointLight.getAttenuation().exponent);
				setUniform3f(pointLightUniforms[5], pointLight.getPosition());
				setUniformf(pointLightUniforms[6], pointLight.getRange());
			}
		}
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transform, matrix);
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		super.setMaterial(material);
		setUniformf(uniform_specularIntensity, material.specularIntesity);
		setUniformf(uniform_specularPower, material.specularPower);
	}

	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
		changedAmbientLight = true;
	}

	public void setAmbientLight(float r, float g, float b) {
		this.ambientLight = new Vector3f(r, g, b);
		changedAmbientLight = true;
	}

	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public void addPointLight(PointLight pointLight) {
		pointLights.add(pointLight);
		pointLight.setChanged();
	}

	public void removePointLight(PointLight pointLight) {
		pointLights.remove(pointLight);
		pointLight.setChanged();
	}

	public List<PointLight> getPointLights() {
		return Collections.unmodifiableList(pointLights);
	}
}