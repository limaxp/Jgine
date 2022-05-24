package org.jgine.render.shader;

public class PostProcessKernel {

	public static final float[] IDENTITY = { 0, 0, 0, 0, 1, 0, 0, 0, 0 };

	public static final float[] SHARPEN = { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

	public static final float[] BLUR = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f, 0.0625f, 0.125f, 0.0625f };

	public static final float[] EDGE_DETECTION = { 1, 1, 1, 1, -8, 1, 1, 1, 1 };

	public static final float[] BOX_BLUR = { 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f,
			0.11111f, 0.11111f };
}
