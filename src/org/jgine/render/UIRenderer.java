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

public class UIRenderer extends Renderer {

	private static final double MIN_DEPTH_VALUE = -0.9999000;
	private static final double DEPTH_SIZE = 0.0000001;

	private static float calculateDepth(int depth) {
		return (float) (MIN_DEPTH_VALUE - (depth * DEPTH_SIZE));
	}

	public static void render(Matrix transform, Model model, int depth) {
		Mesh[] meshes = model.getMeshes();
		Material[] materials = model.getMaterials();
		for (int i = 0; i < meshes.length; i++)
			render(transform, meshes[i], materials[i], depth);
	}

	public static void render(Matrix transform, Mesh mesh, Material material, int depth) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		mvp.m23 = calculateDepth(depth);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		drawIndexed(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material, int depth) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		mvp.m23 = calculateDepth(depth);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, TileMapMesh tileMap, Material material, int depth) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		mvp.m23 = calculateDepth(depth);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapMeshLayer layer = tileMap.getLayer(i);
			drawInstanced(layer.getVao(), layer.mode, layer.getSize(), amount);
		}
	}

	public static void render(Matrix transform, ParticleMesh particle, Material material, int depth) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		mvp.m23 = calculateDepth(depth);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(particle.getVao(), Mesh.POINTS, particle.getInstanceSize());
	}

	public static void renderQuad(Matrix transform, Material material, int depth) {
		render(transform, QUAD_MESH, material, depth);
	}

	public static void renderCube(Matrix transform, Material material, int depth) {
		render(transform, CUBE_MESH, material, depth);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2,
			int depth) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, x2, y2);
		render(transform, mesh, material, depth);
		deleteTempMesh(mesh);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2, int depth) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, z1, x2, y2, z2);
		render(transform, mesh, material, depth);
		deleteTempMesh(mesh);
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points, int depth) {
		BaseMesh mesh = MeshGenerator.line(3, loop, points);
		render(transform, mesh, material, depth);
		deleteTempMesh(mesh);
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points, int depth) {
		BaseMesh mesh = MeshGenerator.line(2, loop, points);
		render(transform, mesh, material, depth);
		deleteTempMesh(mesh);
	}
}
