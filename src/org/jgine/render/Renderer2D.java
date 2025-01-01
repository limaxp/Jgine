package org.jgine.render;

import org.jgine.render.material.Material;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.Mesh;
import org.jgine.render.mesh.MeshGenerator;
import org.jgine.render.mesh.Model;
import org.jgine.render.mesh.TileMapMesh;
import org.jgine.render.mesh.TileMapMesh.TileMapMeshLayer;
import org.jgine.render.mesh.particle.ParticleMesh;
import org.jgine.utils.math.Matrix;

public class Renderer2D extends Renderer {

	private static final float DEPTH_DIVISOR = 16777215.0f;
	private static final float MAX_DEPTH_VALUE = 0.9999999f;

	public static void render(Matrix transform, Model model) {
		render(transform, model, transform.m13 / DEPTH_DIVISOR);
	}

	public static void render(Matrix transform, Model model, float depth) {
		Mesh[] meshes = model.getMeshes();
		Material[] materials = model.getMaterials();
		for (int i = 0; i < meshes.length; i++)
			render(transform, meshes[i], materials[i], depth);
	}

	public static void render(Matrix transform, Mesh mesh, Material material) {
		render(transform, mesh, material, transform.m13 / DEPTH_DIVISOR);
	}

	public static void render(Matrix transform, Mesh mesh, Material material, float depth) {
		Matrix mvp = new Matrix(transform).mult(camera.getMatrix());
		mvp.m23 = depth;
		material.bind(shader);
		shader.setTransform(transform, mvp);
		drawIndexed(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material) {
		render(transform, mesh, material, transform.m13 / DEPTH_DIVISOR);
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material, float depth) {
		Matrix mvp = new Matrix(transform).mult(camera.getMatrix());
		mvp.m23 = depth;
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, TileMapMesh tileMap, Material material) {
		render(transform, tileMap, material, MAX_DEPTH_VALUE);
	}

	public static void render(Matrix transform, TileMapMesh tileMap, Material material, float depth) {
		Matrix mvp = new Matrix(transform).mult(camera.getMatrix());
		mvp.m23 = depth;
		material.bind(shader);
		shader.setTransform(transform, mvp);
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapMeshLayer layer = tileMap.getLayer(i);
			drawInstanced(layer.getVao(), layer.mode, layer.getSize(), amount);
		}
	}

	public static void render(Matrix transform, ParticleMesh particle, Material material) {
		render(transform, particle, material, transform.m13 / DEPTH_DIVISOR);
	}

	public static void render(Matrix transform, ParticleMesh particle, Material material, float depth) {
		Matrix mvp = new Matrix(transform).mult(camera.getMatrix());
		mvp.m23 = depth;
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(particle.getVao(), Mesh.POINTS, particle.getInstanceSize());
	}

	public static void renderQuad(Matrix transform, Material material) {
		renderQuad(transform, material, transform.m13 / DEPTH_DIVISOR);
	}

	public static void renderQuad(Matrix transform, Material material, float depth) {
		render(transform, QUAD_MESH, material, depth);
	}

	public static void renderCube(Matrix transform, Material material) {
		renderCube(transform, material, transform.m13 / DEPTH_DIVISOR);
	}

	public static void renderCube(Matrix transform, Material material, float depth) {
		render(transform, CUBE_MESH, material, depth);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2,
			float depth) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, x2, y2);
		render(transform, mesh, material, depth);
		RenderQueue.deleteTempMesh(mesh);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2, float depth) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, z1, x2, y2, z2);
		render(transform, mesh, material, depth);
		RenderQueue.deleteTempMesh(mesh);
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points, float depth) {
		BaseMesh mesh = MeshGenerator.line(3, loop, points);
		render(transform, mesh, material, depth);
		RenderQueue.deleteTempMesh(mesh);
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points, float depth) {
		BaseMesh mesh = MeshGenerator.line(2, loop, points);
		render(transform, mesh, material, depth);
		RenderQueue.deleteTempMesh(mesh);
	}
}
