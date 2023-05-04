package org.jgine.render.shader;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.list.arrayList.unordered.UnorderedArrayList;
import org.jgine.render.material.Material;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.Matrix;

public class PostProcessShader extends Shader {

	public static final float DEFAULT_SHAKE_STRENGTH = 0.01f;
	public static final int MAX_KERNELS = 4;
	public static final float offset = 1.0f / 300.0f;
	private static final float[] OFFSETS = { -offset, offset, // top-left
			0.0f, offset, // top-center
			offset, offset, // top-right
			-offset, 0.0f, // center-left
			0.0f, 0.0f, // center-center
			offset, 0.0f, // center - right
			-offset, -offset, // bottom-left
			0.0f, -offset, // bottom-center
			offset, -offset // bottom-right
	};

	public final int uniform_transformProjected = addUniform("transformProjected");
	public final int uniform_uTexture = addUniform("uTexture");
	public final int uniform_baseColor = addUniform("baseColor");
	public final int uniform_offsets = addUniform("offsets");
	public final int uniform_kernelSize = addUniform("kernelSize");
	public final int[] uniform_kernel = addUniforms("kernel", MAX_KERNELS);
	public final int uniform_time = addUniform("time");
	public final int uniform_shakeStrength = addUniform("shakeStrength");

	private List<float[]> kernel;
	private boolean changedKernel = false;
	private float shakeTime;
	private float shakeStrength;

	public PostProcessShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super(vertex, geometry, fragment);
		kernel = new UnorderedArrayList<float[]>(MAX_KERNELS);

		bind();
		setUniform2f(uniform_offsets, OFFSETS);
	}

	public void update(float dt) {
		if (shakeTime > 0)
			shakeTime -= dt;
		else
			shakeStrength = 0.0f;
	}

	@Override
	public void bind() {
		super.bind();
		if (changedKernel) {
			changedKernel = false;
			int kernelSize = FastMath.min(this.kernel.size(), MAX_KERNELS);
			setUniformi(uniform_kernelSize, kernelSize);
			for (int i = 0; i < kernelSize; i++)
				setUniformMatrix3f(uniform_kernel[i], kernel.get(i));
		}
		setUniformf(uniform_time, shakeTime);
		setUniformf(uniform_shakeStrength, shakeStrength);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
		setUniformi(uniform_uTexture, 0);
		setUniformRGBA(uniform_baseColor, material.color);
	}

	public void addKernel(float[] kernel) {
		this.kernel.add(kernel);
		changedKernel = true;
	}

	public void removeKernel(float[] kernel) {
		this.kernel.remove(kernel);
		changedKernel = true;
	}

	public void setKernel(List<float[]> kernel) {
		this.kernel = kernel;
		changedKernel = true;
	}

	public List<float[]> getKernel() {
		return Collections.unmodifiableList(kernel);
	}

	public void shake(float time) {
		shake(time, DEFAULT_SHAKE_STRENGTH);
	}

	public void shake(float time, float strength) {
		this.shakeTime = time;
		this.shakeStrength = strength;
	}
}
