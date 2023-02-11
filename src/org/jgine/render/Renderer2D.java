package org.jgine.render;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.Mesh;
import org.jgine.render.mesh.Model;
import org.jgine.render.mesh.TileMap;
import org.jgine.render.shader.Shader;

public class Renderer2D extends Renderer {

	public static void render(Matrix transform, Model model) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		model.render(shader);
	}

	public static void render(Matrix transform, Mesh mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, TileMap tileMap, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		tileMap.render();
	}

	public static void renderQuad(Matrix transform, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		QUAD_MESH.render();
	}

	public static void renderCube(Matrix transform, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		CUBE_MESH.render();
	}

	public static void renderCircle(Matrix transform, Material material) {
		Shader tmp = shader;
		setShader(CIRCLE_SHADER);
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		QUAD_MESH.render();
		setShader(tmp);
	}

	public static void renderLine(Matrix transform, Material material, Vector2f start, Vector2f end) {
		renderLine(transform, material, start.x, start.y, end.x, end.y);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(2, false)) {
			lineMesh.loadVertices(new float[] { x1, y1, x2, y2 }, null);
			lineMesh.mode = Mesh.LINES;
			lineMesh.render();
		}
	}

	public static void renderLine(Matrix transform, Material material, Vector3f start, Vector3f end) {
		renderLine(transform, material, start.x, start.y, start.z, end.x, end.y, end.z);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(3, false)) {
			lineMesh.loadVertices(new float[] { x1, y1, z1, x2, y2, z2 }, null);
			lineMesh.mode = Mesh.LINES;
			lineMesh.render();
		}
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(3, false)) {
			lineMesh.loadVertices(points, null);
			if (loop)
				lineMesh.mode = Mesh.LINE_LOOP;
			else
				lineMesh.mode = Mesh.LINE_STRIP;
			lineMesh.render();
		}
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(2, false)) {
			lineMesh.loadVertices(points, null);
			if (loop)
				lineMesh.mode = Mesh.LINE_LOOP;
			else
				lineMesh.mode = Mesh.LINE_STRIP;
			lineMesh.render();
		}
	}

}
