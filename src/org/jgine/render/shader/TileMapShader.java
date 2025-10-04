package org.jgine.render.shader;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.light.PointLight;
import org.jgine.render.material.Material;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector4f;

public class TileMapShader extends Shader {

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_textureColums = addUniform("textureColums");
	public final int uniform_textureRows = addUniform("textureRows");
	public final int uniform_color = addUniform("baseColor");

	public final int uniform_transform = addUniform("transform");
	public final int uniform_ambientLight = addUniform("ambientLight");
	public final int uniform_pointLightSize = addUniform("pointLightSize");
	protected final int uniforms_pointLights[][] = addUniforms("pointLights", PhongShader.MAX_POINT_LIGHTS,
			new String[] { "base.color", "base.intensity", "atten.constant", "atten.linear", "atten.exponent", "pos",
					"range" });

	public TileMapShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super(vertex, geometry, fragment);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
		setUniformMatrix4f(uniform_transform, matrix);
	}

	@Override
	public void setMaterial(Material material) {
		setUniformi(uniform_uTexture, 0);
		setUniformRGBA(uniform_color, material.color);
	}

	public void setTileMapData(int colums, int rows) {
		setUniformi(uniform_textureColums, colums);
		setUniformi(uniform_textureRows, rows);
	}

	public void setAmbientLight(int color) {
		setAmbientLight(Vector4f.fromColor(color));
	}

	public void setAmbientLight(Vector4f color) {
		setUniform3f(uniform_ambientLight, color);
	}

	public void setPointLights(List<PointLight> pointLights) {
		int pointLightSize = FastMath.min(pointLights.size(), PhongShader.MAX_POINT_LIGHTS);
		setUniformi(uniform_pointLightSize, pointLightSize);
		for (int i = 0; i < pointLightSize; i++) {
			PointLight pointLight = pointLights.get(i);
			int[] pointLightUniforms = uniforms_pointLights[i];
			Vector4f color2 = Vector4f.fromColor(pointLight.getColor());
			setUniform3f(pointLightUniforms[0], color2.x, color2.y, color2.z);
			setUniformf(pointLightUniforms[1], pointLight.getIntensity());
			setUniformf(pointLightUniforms[2], pointLight.getAttenuation().constant);
			setUniformf(pointLightUniforms[3], pointLight.getAttenuation().linear);
			setUniformf(pointLightUniforms[4], pointLight.getAttenuation().exponent);
			setUniform3f(pointLightUniforms[5], pointLight.getPosition());
			setUniformf(pointLightUniforms[6], pointLight.getRange());
		}
	}
}