package org.jgine.render;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.jgine.render.graphic.mesh.BaseMesh2D;
import org.jgine.render.graphic.mesh.Mesh;
import org.jgine.render.graphic.mesh.Mesh2D;
import org.jgine.render.graphic.mesh.MeshMode;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.render.graphic.mesh.Model2D;

public class UIRenderer extends Renderer {

	private static final Matrix UI_MATRIX = Matrix.asOrthographic(-1, 1, -1, 1, -1, 1);

	public static void render(Matrix transform, Model model) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		for (Mesh mesh : model) {
			mesh.material.bind(shader);
			mesh.render();
		}
	}

	public static void render(Matrix transform, Mesh mesh) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		mesh.material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, Model2D model, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		for (Mesh2D mesh : model)
			mesh.render();
	}

	public static void render(Matrix transform, Mesh2D mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, BaseMesh2D mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		mesh.render();
	}

	public static void renderQuad(Matrix transform, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		PLANE_MESH.render();
	}

	public static void renderQuadFlipped(Matrix transform, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		PLANE_MESH_FLIPPED.render();
	}

	public static void renderLine(Matrix transform, Vector3f start, Vector3f end, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(new float[] { start.x, start.y, start.z, end.x, end.y, end.z })) {
			lineMesh.setMode(MeshMode.LINES);
			lineMesh.render();
		}
	}

	public static void renderLine(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(points)) {
			if (loop)
				lineMesh.setMode(MeshMode.LINE_LOOP);
			else
				lineMesh.setMode(MeshMode.LINE_STRIP);
			lineMesh.render();
		}
	}
}
