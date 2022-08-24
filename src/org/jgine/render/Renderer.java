package org.jgine.render;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.TileMap;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.jgine.render.graphic.mesh.BaseMesh2D;
import org.jgine.render.graphic.mesh.Mesh;
import org.jgine.render.graphic.mesh.Mesh2D;
import org.jgine.render.graphic.mesh.MeshMode;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.render.graphic.mesh.Model2D;
import org.jgine.render.graphic.particle.BillboardParticle;
import org.jgine.render.shader.BasicShader;
import org.jgine.render.shader.BillboardParticleShader;
import org.jgine.render.shader.CircleShader;
import org.jgine.render.shader.PhongShader;
import org.jgine.render.shader.PostProcessShader;
import org.jgine.render.shader.Shader;
import org.jgine.render.shader.TextureShader;
import org.jgine.render.shader.TileMapShader;
import org.jgine.system.systems.camera.Camera;

public class Renderer {

	static {
		OpenGL.init();
	}

	public static final BasicShader BASIC_SHADER = new BasicShader("Basic");
	public static final TextureShader TEXTURE_SHADER = new TextureShader("Texture");
	public static final PhongShader PHONG_SHADER = new PhongShader("Phong");
	public static final BillboardParticleShader PARTICLE_SHADER = new BillboardParticleShader("BillboardParticle");
	public static final TileMapShader TILE_MAP_SHADER = new TileMapShader("TileMap");
	public static final CircleShader CIRCLE_SHADER = new CircleShader("Circle");
	public static final PostProcessShader POST_PROCESS_SHADER = new PostProcessShader("PostProcess");

	protected static Shader shader = Shader.NULL;
	protected static Camera camera;
	protected static RenderTarget renderTarget = RenderTarget.NONE;

	protected static final BaseMesh2D QUAD_MESH;
	protected static final Mesh CUBE_MESH;

	static {
		QUAD_MESH = new BaseMesh2D(new float[] { -1, 1, 1, 1, -1, -1, 1, -1 }, new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		QUAD_MESH.setMode(MeshMode.TRIANGLE_STRIP);

		CUBE_MESH = new Mesh(
				new float[] { 1, 1, 1, -1, 1, 1, 1, 1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, -1 },
				new int[] { 2, 1, 0, 3, 1, 2, 0, 1, 4, 5, 4, 1, 6, 3, 2, 6, 7, 3, 1, 3, 5, 5, 3, 7, 0, 4, 6, 0, 6, 2, 4,
						5, 6, 7, 6, 5, });
	}

	public static void init() {
	}

	public static void terminate() {
		QUAD_MESH.close();
		CUBE_MESH.close();
		BillboardParticle.free();

		OpenGL.terminate();
	}

	public static void begin() {
		OpenGL.clearFrameBuffer();
	}

	public static void end() {
		if (renderTarget != RenderTarget.NONE) {
			RenderTarget temp = renderTarget;
			setRenderTarget(RenderTarget.NONE);
			setShader(UIRenderer.POST_PROCESS_SHADER);
			UIRenderer.renderQuad(Transform.calculateMatrix(new Matrix(), Vector3f.NULL, Vector3f.FULL),
					new Material(temp));
			setRenderTarget(temp);
		}

		Engine.getInstance().getWindow().swapBuffers();
	}

	public static void render(Matrix transform, Model model) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		for (Mesh mesh : model) {
			mesh.material.bind(shader);
			mesh.render();
		}
	}

	public static void render(Matrix transform, Mesh mesh) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		mesh.material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, Model2D model, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		for (Mesh2D mesh : model)
			mesh.render();
	}

	public static void render(Matrix transform, Mesh2D mesh, Material material) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		mesh.render();
	}

	public static void render(Matrix transform, BaseMesh2D mesh, Material material) {
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
		try (BaseMesh lineMesh = new BaseMesh(new float[] { start.x, start.y, start.z, end.x, end.y, end.z })) {
			lineMesh.setMode(MeshMode.LINES);
			lineMesh.render();
		}
	}

	public static void renderLine3d(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh(points)) {
			if (loop)
				lineMesh.setMode(MeshMode.LINE_LOOP);
			else
				lineMesh.setMode(MeshMode.LINE_STRIP);
			lineMesh.render();
		}
	}

	public static void renderLine2d(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh2D lineMesh = new BaseMesh2D(points)) {
			if (loop)
				lineMesh.setMode(MeshMode.LINE_LOOP);
			else
				lineMesh.setMode(MeshMode.LINE_STRIP);
			lineMesh.render();
		}
	}

	public static void setShader(Shader shader) {
		if (Renderer.shader != shader) {
			Renderer.shader.unbind();
			Renderer.shader = shader;
			shader.bind();
		}
	}

	public static Shader getShader() {
		return shader;
	}

	public static void setCamera(Camera camera) {
		Renderer.camera = camera;
	}

	public static Camera getCamera() {
		return camera;
	}

	public static void setRenderTarget(RenderTarget renderTarget) {
		if (Renderer.renderTarget != renderTarget) {
			Renderer.renderTarget.end();
			Renderer.renderTarget = renderTarget;
			renderTarget.begin();
		}
	}

	public static RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public static void enableDepthTest() {
		OpenGL.enableDepthTest();
	}

	public static void disableDepthTest() {
		OpenGL.disableDepthTest();
	}

	public static void enableWireframeMode() {
		OpenGL.enableWireframeMode();
	}

	public static void disableWireframeMode() {
		OpenGL.disableWireframeMode();
	}
}
