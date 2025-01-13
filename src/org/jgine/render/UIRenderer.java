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

	public static void render(Matrix transform, Model model) {
		Mesh[] meshes = model.getMeshes();
		Material[] materials = model.getMaterials();
		for (int i = 0; i < meshes.length; i++)
			render(transform, meshes[i], materials[i]);
	}

	public static void render(Matrix transform, Mesh mesh, Material material) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		drawIndexed(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, TileMapMesh tileMap, Material material) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapMeshLayer layer = tileMap.getLayer(i);
			drawInstanced(layer.getVao(), layer.mode, layer.getSize(), amount);
		}
	}

	public static void render(Matrix transform, ParticleMesh particle, Material material) {
		Matrix mvp = new Matrix(transform).mult(UI_MATRIX);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(particle.getVao(), Mesh.POINTS, particle.getInstanceSize());
	}

	public static void renderQuad(Matrix transform, Material material) {
		render(transform, QUAD_MESH, material);
	}

	public static void renderCube(Matrix transform, Material material) {
		render(transform, CUBE_MESH, material);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, x2, y2);
		render(transform, mesh, material);
		deleteTempMesh(mesh);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2) {
		BaseMesh mesh = MeshGenerator.line(x1, y1, z1, x2, y2, z2);
		render(transform, mesh, material);
		deleteTempMesh(mesh);
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points) {
		BaseMesh mesh = MeshGenerator.line(3, loop, points);
		render(transform, mesh, material);
		deleteTempMesh(mesh);
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points) {
		BaseMesh mesh = MeshGenerator.line(2, loop, points);
		render(transform, mesh, material);
		deleteTempMesh(mesh);
	}
}
