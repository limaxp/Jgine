package org.jgine.render;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.RenderTarget.Attachment;
import org.jgine.render.light.PointLight;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.Mesh;
import org.jgine.render.mesh.MeshGenerator;
import org.jgine.render.mesh.Model;
import org.jgine.render.mesh.TileMap;
import org.jgine.render.shader.BasicShader;
import org.jgine.render.shader.BillboardParticleShader;
import org.jgine.render.shader.ComputeShader;
import org.jgine.render.shader.Phong2dShader;
import org.jgine.render.shader.PhongShader;
import org.jgine.render.shader.PostProcessShader;
import org.jgine.render.shader.Shader;
import org.jgine.render.shader.TextureShader;
import org.jgine.render.shader.TileMapShader;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.light.LightScene;
import org.jgine.utils.Color;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector4f;
import org.jgine.utils.options.Options;

public class Renderer {

	public static final BasicShader BASIC_SHADER;
	public static final TextureShader TEXTURE_SHADER;
	public static final PhongShader PHONG_SHADER;
	public static final Phong2dShader PHONG_2D_SHADER;
	public static final BillboardParticleShader PARTICLE_SHADER;
	public static final TileMapShader TILE_MAP_SHADER;
	public static final PostProcessShader POST_PROCESS_SHADER;
	public static final ComputeShader BASIC_COMPUTE_SHADER;

	protected static final RenderTarget POST_PROCESS_TARGET;
	protected static final BaseMesh QUAD_MESH;
	protected static final Mesh CUBE_MESH;

	protected static Shader shader = Shader.NULL;
	protected static Camera camera;
	protected static RenderTarget renderTarget;

	static {
		OpenGL.init();

		BASIC_SHADER = new BasicShader("Basic");
		TEXTURE_SHADER = new TextureShader("Texture");
		PHONG_SHADER = new PhongShader("Phong");
		PHONG_2D_SHADER = new Phong2dShader("Phong2d");
		PARTICLE_SHADER = new BillboardParticleShader("BillboardParticle");
		TILE_MAP_SHADER = new TileMapShader("TileMap");
		POST_PROCESS_SHADER = new PostProcessShader("PostProcess");
		BASIC_COMPUTE_SHADER = new ComputeShader("BasicCompute");

		POST_PROCESS_TARGET = new RenderTarget();
		POST_PROCESS_TARGET.bind();
		POST_PROCESS_TARGET.setTexture(Texture.RGB, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt());
		POST_PROCESS_TARGET.checkStatus();
		POST_PROCESS_TARGET.unbind();

		QUAD_MESH = MeshGenerator.quad(1.0f);
		CUBE_MESH = MeshGenerator.cube(1.0f);
	}

	public static void init() {
	}

	public static void terminate() {
		QUAD_MESH.close();
		CUBE_MESH.close();
		OpenGL.terminate();
	}

	public static void renderFrame(List<RenderConfiguration> renderConfigs) {
		for (RenderConfiguration renderConfig : renderConfigs) {
			RenderTarget configTarget = renderConfig.getRenderTarget();
			RenderTarget intermediateTarget = renderConfig.getIntermediateTarget();
			Attachment attachment = configTarget.getAttachment(RenderTarget.COLOR_ATTACHMENT0);
			int resolutionX = attachment.getWidth();
			int resolutionY = attachment.getHeight();

			configTarget.bindRead();
			intermediateTarget.bindDraw();
			RenderTarget.blit(0, 0, resolutionX, resolutionY, 0, 0, resolutionX, resolutionY,
					RenderTarget.COLOR_BUFFER_BIT, Texture.NEAREST);

			intermediateTarget.bindRead();
			POST_PROCESS_TARGET.bindDraw();
			RenderTarget.blit(0, 0, resolutionX, resolutionY, (int) (renderConfig.getX() * resolutionX),
					(int) (renderConfig.getY() * resolutionY), (int) (renderConfig.getWidth() * resolutionX),
					(int) (renderConfig.getHeight() * resolutionY), RenderTarget.COLOR_BUFFER_BIT, Texture.NEAREST);
		}

		setShader(POST_PROCESS_SHADER);
		RenderTarget temp = Renderer.renderTarget;
		setRenderTarget(null);
		UIRenderer.renderQuad(Transform.calculateMatrix(new Matrix(), 0, 0, 0, 1, 1, 0),
				new Material(POST_PROCESS_TARGET.getTexture(RenderTarget.COLOR_ATTACHMENT0)));
		setRenderTarget(temp);
	}

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
		if (Renderer.camera != camera)
			Renderer.camera = camera;
		RenderTarget renderTarget = camera.getRenderTarget();
		setRenderTarget(renderTarget);
		renderTarget.clear();
	}

	public static Camera getCamera() {
		return camera;
	}

	public static void setRenderTarget(@Nullable RenderTarget renderTarget) {
		if (Renderer.renderTarget != renderTarget) {
			if (renderTarget == null)
				Renderer.renderTarget.unbindViewport();
			else
				renderTarget.bindViewport(RenderTarget.COLOR_ATTACHMENT0);
			Renderer.renderTarget = renderTarget;
		}
	}

	@Nullable
	public static RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public static void setLights(LightScene lightScene) {
		Vector4f ambientLight = Color.toVector(lightScene.getAmbientLight());
		List<PointLight> pointLights = lightScene.getPointLights();
		PHONG_SHADER.bind();
		PHONG_SHADER.setAmbientLight(ambientLight);
		PHONG_SHADER.setPointLights(pointLights);
		PHONG_SHADER.setDirectionalLight(lightScene.getDirectionalLight());
		PHONG_SHADER.setCameraPosition(camera);

		PHONG_2D_SHADER.bind();
		PHONG_2D_SHADER.setAmbientLight(ambientLight);
		PHONG_2D_SHADER.setPointLights(pointLights);

		TILE_MAP_SHADER.bind();
		TILE_MAP_SHADER.setAmbientLight(ambientLight);
		TILE_MAP_SHADER.setPointLights(pointLights);
	}

	public static void enableWireframeMode() {
		OpenGL.enableWireframeMode();
	}

	public static void disableWireframeMode() {
		OpenGL.disableWireframeMode();
	}

	public static void enableDepthTest() {
		OpenGL.enableDepthTest();
	}

	public static void disableDepthTest() {
		OpenGL.disableDepthTest();
	}

	/**
	 * Disable this to not write to depth buffer. Can be used to draw transparent
	 * objects!
	 */
	public static void enableDepthMask() {
		OpenGL.enableDepthMask();
	}

	/**
	 * Disable this to not write to depth buffer. Can be used to draw transparent
	 * objects!
	 */
	public static void disableDepthMask() {
		OpenGL.disableDepthMask();
	}

	public static void setClearColor(Vector3f color) {
		OpenGL.setClearColor(color.x, color.y, color.z, 1.0f);
	}

	public static void setClearColor(Vector4f color) {
		OpenGL.setClearColor(color.x, color.y, color.z, color.w);
	}

	public static void setClearColor(float red, float green, float blue) {
		OpenGL.setClearColor(red, green, blue, 1.0f);
	}

	public static void setClearColor(float red, float green, float blue, float alpha) {
		OpenGL.setClearColor(red, green, blue, alpha);
	}

	public static void setClearColor(int color) {
		OpenGL.setClearColor((float) Color.red(color) / 255, (float) Color.green(color) / 255,
				(float) Color.blue(color) / 255, (float) Color.alpha(color) / 255);
	}
}
