package org.jgine.render.shader;

public class PostProcessKernel {

	public static final float[] SHARPEN = new float[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

	public static final float[] BLUR = new float[] { 1.0f / 16, 2.0f / 16, 1.0f / 16, 2.0f / 16, 4.0f / 16, 2.0f / 16,
			1.0f / 16, 2.0f / 16, 1.0f / 16 };

	public static final float[] EDGE_DETECTION = new float[] { 1, 1, 1, 1, -8, 1, 1, 1, 1 };
}
