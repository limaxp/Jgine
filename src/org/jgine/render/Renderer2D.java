package org.jgine.render;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.TileMap;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.jgine.render.graphic.mesh.Mesh;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.render.graphic.particle.BillboardParticle;
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

	public static void render(Matrix transform, BillboardParticle particle, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		particle.render();
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

	public static void renderLine(Matrix transform, Vector3f start, Vector3f end, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh()) {
			lineMesh.loadData(3, new float[] { start.x, start.y, start.z, end.x, end.y, end.z }, null);
			lineMesh.mode = Mesh.LINES;
			lineMesh.render();
		}
	}

	public static void renderLine3d(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh()) {
			lineMesh.loadData(3, points, null);
			if (loop)
				lineMesh.mode = Mesh.LINE_LOOP;
			else
				lineMesh.mode = Mesh.LINE_STRIP;
			lineMesh.render();
		}
	}

	public static void renderLine2d(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh()) {
			lineMesh.loadData(2, points, null);
			if (loop)
				lineMesh.mode = Mesh.LINE_LOOP;
			else
				lineMesh.mode = Mesh.LINE_STRIP;
			lineMesh.render();
		}
	}

}
