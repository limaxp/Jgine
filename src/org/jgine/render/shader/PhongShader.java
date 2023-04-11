package org.jgine.render.shader;

import java.util.List;

import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.PointLight;
import org.jgine.render.material.Material;
import org.jgine.system.systems.camera.Camera;
import org.jgine.utils.Color;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector4f;

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

	public PhongShader(String name) {
		super(name);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		super.setTransform(matrix, projectionMatrix);
		setUniformMatrix4f(uniform_transform, matrix);
	}

	@Override
	public void setMaterial(Material material) {
		super.setMaterial(material);
		setUniformf(uniform_specularIntensity, material.specularIntesity);
		setUniformf(uniform_specularPower, material.specularPower);
	}

	public void setAmbientLight(int color) {
		setAmbientLight(Color.toVector(color));
	}

	public void setAmbientLight(Vector4f color) {
		setUniform3f(uniform_ambientLight, color);
	}

	public void setPointLights(List<PointLight> pointLights) {
		int pointLightSize = FastMath.min(pointLights.size(), MAX_POINT_LIGHTS);
		setUniformi(uniform_pointLightSize, pointLightSize);
		for (int i = 0; i < pointLightSize; i++) {
			PointLight pointLight = pointLights.get(i);
			int[] pointLightUniforms = uniforms_pointLights[i];
			Vector4f color2 = Color.toVector(pointLight.getColor());
			setUniform3f(pointLightUniforms[0], color2.x, color2.y, color2.z);
			setUniformf(pointLightUniforms[1], pointLight.getIntensity());
			setUniformf(pointLightUniforms[2], pointLight.getAttenuation().constant);
			setUniformf(pointLightUniforms[3], pointLight.getAttenuation().linear);
			setUniformf(pointLightUniforms[4], pointLight.getAttenuation().exponent);
			setUniform3f(pointLightUniforms[5], pointLight.getPosition());
			setUniformf(pointLightUniforms[6], pointLight.getRange());
		}
	}

	public void setDirectionalLight(DirectionalLight directionalLight) {
		Vector4f color = Color.toVector(directionalLight.getColor());
		setUniform3f(uniform_directionalLight_color, color.x, color.y, color.z);
		setUniformf(uniform_directionalLight_intensity, directionalLight.getIntensity());
		setUniform3f(uniform_directionalLight_direction, directionalLight.getDirection());
	}

	public void setCameraPosition(Camera camera) {
		setUniform3f(uniform_camPos, camera.getTransform().getPosition());
	}
}