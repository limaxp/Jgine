package org.jgine.render;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.Mesh;
import org.jgine.render.graphic.mesh.Mesh2D;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.render.graphic.mesh.Model2D;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.jgine.render.graphic.mesh.BaseMesh2D;

public class UIRenderer extends Renderer {

	public static void render(Matrix transform, Model model) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		for (Mesh mesh : model) {
			mesh.material.bind(shader);
			mesh.render();
		}
	}

	public static void render(Matrix transform, Mesh mesh) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		mesh.material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, Model2D model, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		for (Mesh2D mesh : model)
			mesh.render();
	}

	public static void render(Matrix transform, Mesh2D mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, BaseMesh2D mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		mesh.render();
	}

	public static void renderQuad(Matrix transform, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		PLANE_MESH.render();
	}

	public static void renderLine(Matrix transform, Vector3f start, Vector3f end, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(
				new float[] {
						start.x, start.y, start.z,
						end.x, end.y, end.z
				})) {
			lineMesh.setMode(Mesh.Mode.LINES);
			lineMesh.render();
		}
	}

	public static void renderLine(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getOrthographicMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(points)) {
			if (loop)
				lineMesh.setMode(Mesh.Mode.LINE_LOOP);
			else
				lineMesh.setMode(Mesh.Mode.LINE_STRIP);
			lineMesh.render();
		}
	}
}
