package org.jgine.render;

import org.jgine.render.material.Material;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.Mesh;
import org.jgine.render.mesh.Model;
import org.jgine.render.mesh.TileMap;
import org.jgine.render.mesh.TileMap.TileMapLayer;
import org.jgine.render.mesh.particle.BillboardParticle;
import org.jgine.utils.math.Matrix;

public class UIRenderer extends Renderer {

	private static final double MIN_DEPTH_VALUE = -0.9999900;
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
		RenderQueue.render(mesh.getVao(), mesh.mode, 0, mesh.getSize(), transform, UI_MATRIX, material, renderTarget,
				shader, calculateDepth(depth));
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material, int depth) {
		RenderQueue.render(mesh.getVao(), mesh.mode, mesh.getSize(), 0, transform, UI_MATRIX, material, renderTarget,
				shader, calculateDepth(depth));
	}

	public static void render(Matrix transform, TileMap tileMap, Material material, int depth) {
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapLayer layer = tileMap.getLayer(i);
			RenderQueue.renderInstanced(layer.getVao(), layer.mode, layer.getSize(), 0, transform, UI_MATRIX, material,
					renderTarget, shader, amount, calculateDepth(depth));
		}
	}

	public static void render(Matrix transform, BillboardParticle particle, Material material, int depth) {
		RenderQueue.renderInstanced(particle.getVao(), particle.mode, particle.getSize(), 0, transform, UI_MATRIX,
				material, renderTarget, shader, particle.getInstanceSize(), calculateDepth(depth));
	}

	public static void renderQuad(Matrix transform, Material material, int depth) {
		RenderQueue.render(QUAD_MESH.getVao(), QUAD_MESH.mode, QUAD_MESH.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
	}

	public static void renderCube(Matrix transform, Material material, int depth) {
		RenderQueue.render(CUBE_MESH.getVao(), CUBE_MESH.mode, CUBE_MESH.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2,
			int depth) {
		BaseMesh lineMesh = new BaseMesh(2, false);
		lineMesh.loadVertices(new float[] { x1, y1, x2, y2 }, null);
		lineMesh.mode = Mesh.LINES;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2, int depth) {
		BaseMesh lineMesh = new BaseMesh(3, false);
		lineMesh.loadVertices(new float[] { x1, y1, z1, x2, y2, z2 }, null);
		lineMesh.mode = Mesh.LINES;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points, int depth) {
		BaseMesh lineMesh = new BaseMesh(3, false);
		lineMesh.loadVertices(points, null);
		if (loop)
			lineMesh.mode = Mesh.LINE_LOOP;
		else
			lineMesh.mode = Mesh.LINE_STRIP;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points, int depth) {
		BaseMesh lineMesh = new BaseMesh(2, false);
		lineMesh.loadVertices(points, null);
		if (loop)
			lineMesh.mode = Mesh.LINE_LOOP;
		else
			lineMesh.mode = Mesh.LINE_STRIP;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, UI_MATRIX, material,
				renderTarget, shader, calculateDepth(depth));
		RenderQueue.deleteTempMesh(lineMesh);
	}
}
