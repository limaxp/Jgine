package org.jgine.render.shader;

import java.util.List;

import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.Matrix;
import org.jgine.render.graphic.material.Material;

public class PostProcessShader extends Shader {

	private static final int MAX_KERNELS = 4;
	private static final float offset = 1.0f / 300.0f;
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
	public final int uniform_kernel_size = addUniform("kernelSize");
	public final int[] uniform_kernel = addUniforms("kernel", MAX_KERNELS);

	public final List<float[]> kernel;

	public PostProcessShader(String name) {
		super(name);
		kernel = new FastArrayList<>(MAX_KERNELS);
	}

	@Override
	public void bind() {
		super.bind();
		setUniform2f(uniform_offsets, OFFSETS);

		int kernelSize = FastMath.min(kernel.size(), MAX_KERNELS);
		setUniformi(uniform_kernel_size, kernelSize);
		for (int i = 0; i < kernelSize; i++)
			setUniformMatrix3f(uniform_kernel[i], kernel.get(i));
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		setUniformMatrix4f(uniform_transformProjected, projectionMatrix);
	}

	@Override
	public void setMaterial(Material material) {
//		ITexture texture = material.getTexture();
		setUniformi(uniform_uTexture, 0);
		setUniformColor(uniform_baseColor, material.color);
	}
}