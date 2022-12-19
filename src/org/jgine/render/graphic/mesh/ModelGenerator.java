package org.jgine.render.graphic.mesh;

public class ModelGenerator {

	public static BaseMesh quad(float size) {
		BaseMesh mesh = new BaseMesh();
		mesh.loadData(2, new float[] { -size, size, size, size, -size, -size, size, -size },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		mesh.mode = Mesh.TRIANGLE_STRIP;
		return mesh;
	}

	public static Mesh cube(float size) {
		Mesh mesh = new Mesh();
		mesh.loadData(3,
				new float[] { size, size, size, -size, size, size, size, size, -size, -size, size, -size, size, -size,
						size, -size, -size, size, size, -size, -size, -size, -size, -size },
				new int[] { 2, 1, 0, 3, 1, 2, 0, 1, 4, 5, 4, 1, 6, 3, 2, 6, 7, 3, 1, 3, 5, 5, 3, 7, 0, 4, 6, 0, 6, 2, 4,
						5, 6, 7, 6, 5, });
		return mesh;
	}
}
