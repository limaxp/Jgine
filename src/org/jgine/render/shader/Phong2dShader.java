package org.jgine.render.shader;

import java.util.List;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.Color;
import org.jgine.render.light.PointLight;

public class Phong2dShader extends TextureShader {

	public final int uniform_transform = addUniform("transform");
	public final int uniform_ambientLight = addUniform("ambientLight");
	public final int uniform_pointLightSize = addUniform("pointLightSize");
	protected final int uniforms_pointLights[][] = addUniforms("pointLights", PhongShader.MAX_POINT_LIGHTS,
			new String[] { "base.color", "base.intensity", "atten.constant", "atten.linear", "atten.exponent", "pos",
					"range" });

	private int ambientLight;
	private List<PointLight> pointLights;

	public Phong2dShader(String name, List<PointLight> pointLights) {
		super(name);
		ambientLight = Color.BLACK;
		this.pointLights = pointLights;
	}

	@Override
	public void bind() {
		super.bind();

		setUniform3f(uniform_ambientLight, Color.toVector(ambientLight));

		int pointLightSize = FastMath.min(pointLights.size(), PhongShader.MAX_POINT_LIGHTS);
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

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		super.setTransform(matrix, projectionMatrix);
		setUniformMatrix4f(uniform_transform, matrix);
	}

	public void setAmbientLight(int color) {
		this.ambientLight = color;
	}

	public int getAmbientLight() {
		return ambientLight;
	}

	public List<PointLight> getPointLights() {
		return pointLights;
	}
}