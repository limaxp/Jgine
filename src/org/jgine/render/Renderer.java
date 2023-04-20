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
import org.jgine.render.mesh.TileMap.TileMapLayer;
import org.jgine.render.mesh.particle.BillboardParticle;
import org.jgine.render.shader.BasicShader;
import org.jgine.render.shader.BillboardParticleShader;
import org.jgine.render.shader.ComputeShader;
import org.jgine.render.shader.Phong2dShader;
import org.jgine.render.shader.PhongShader;
import org.jgine.render.shader.PostProcessShader;
import org.jgine.render.shader.Shader;
import org.jgine.render.shader.TextShader;
import org.jgine.render.shader.TextureShader;
import org.jgine.render.shader.TileMapShader;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.light.LightScene;
import org.jgine.utils.Color;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector4f;
import org.jgine.utils.options.Options;

public class Renderer {

	protected static final Matrix UI_MATRIX = Matrix.asOrthographic(-1, 1, -1, 1, -1, 1);

	public static final BasicShader BASIC_SHADER;
	public static final TextureShader TEXTURE_SHADER;
	public static final TextShader TEXT_SHADER;
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
		TEXT_SHADER = new TextShader("Text");
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
		RenderTarget.unbind();

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

	public static void start() {
		RenderQueue.start();
	}

	public static void finish(List<RenderConfiguration> renderConfigs) {
		RenderQueue.render();
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

		RenderTarget.unbind();
		POST_PROCESS_SHADER.bind();
		new Material(POST_PROCESS_TARGET.getTexture(RenderTarget.COLOR_ATTACHMENT0)).bind(POST_PROCESS_SHADER);
		Matrix transform = Transform.calculateMatrix(new Matrix(), 0, 0, 0, 1, 1, 0);
		POST_PROCESS_SHADER.setTransform(transform, new Matrix(transform).mult(UI_MATRIX));
		RenderQueue.render(QUAD_MESH.getVao(), QUAD_MESH.mode, QUAD_MESH.getSize(), 0);
	}

	public static void render(Matrix transform, Model model) {
		Mesh[] meshes = model.getMeshes();
		Material[] materials = model.getMaterials();
		for (int i = 0; i < meshes.length; i++)
			render(transform, meshes[i], materials[i]);
	}

	public static void render(Matrix transform, Mesh mesh, Material material) {
		RenderQueue.render(mesh.getVao(), mesh.mode, 0, mesh.getSize(), transform, camera.getMatrix(), material,
				renderTarget, shader);
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material) {
		RenderQueue.render(mesh.getVao(), mesh.mode, mesh.getSize(), 0, transform, camera.getMatrix(), material,
				renderTarget, shader);
	}

	public static void render(Matrix transform, TileMap tileMap, Material material) {
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapLayer layer = tileMap.getLayer(i);
			RenderQueue.renderInstanced(layer.getVao(), layer.mode, layer.getSize(), 0, transform, camera.getMatrix(),
					material, renderTarget, shader, amount);
		}
	}

	public static void render(Matrix transform, BillboardParticle particle, Material material) {
		RenderQueue.renderInstanced(particle.getVao(), particle.mode, particle.getSize(), 0, transform,
				camera.getMatrix(), material, renderTarget, shader, particle.getInstanceSize());
	}

	public static void renderQuad(Matrix transform, Material material) {
		RenderQueue.render(QUAD_MESH.getVao(), QUAD_MESH.mode, QUAD_MESH.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
	}

	public static void renderCube(Matrix transform, Material material) {
		RenderQueue.render(CUBE_MESH.getVao(), CUBE_MESH.mode, CUBE_MESH.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float x2, float y2) {
		BaseMesh lineMesh = new BaseMesh(2, false);
		lineMesh.loadVertices(new float[] { x1, y1, x2, y2 }, null);
		lineMesh.mode = Mesh.LINES;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine(Matrix transform, Material material, float x1, float y1, float z1, float x2, float y2,
			float z2) {
		BaseMesh lineMesh = new BaseMesh(3, false);
		lineMesh.loadVertices(new float[] { x1, y1, z1, x2, y2, z2 }, null);
		lineMesh.mode = Mesh.LINES;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine3d(Matrix transform, Material material, boolean loop, float[] points) {
		BaseMesh lineMesh = new BaseMesh(3, false);
		lineMesh.loadVertices(points, null);
		if (loop)
			lineMesh.mode = Mesh.LINE_LOOP;
		else
			lineMesh.mode = Mesh.LINE_STRIP;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void renderLine2d(Matrix transform, Material material, boolean loop, float[] points) {
		BaseMesh lineMesh = new BaseMesh(2, false);
		lineMesh.loadVertices(points, null);
		if (loop)
			lineMesh.mode = Mesh.LINE_LOOP;
		else
			lineMesh.mode = Mesh.LINE_STRIP;
		RenderQueue.render(lineMesh.getVao(), lineMesh.mode, lineMesh.getSize(), 0, transform, camera.getMatrix(),
				material, renderTarget, shader);
		RenderQueue.deleteTempMesh(lineMesh);
	}

	public static void setShader(Shader shader) {
		Renderer.shader = shader;
	}

	public static Shader getShader() {
		return shader;
	}

	public static void setCamera(Camera camera) {
		Renderer.camera = camera;
		setRenderTarget(camera.getRenderTarget());
	}

	public static Camera getCamera() {
		return camera;
	}

	public static void setRenderTarget(@Nullable RenderTarget renderTarget) {
		Renderer.renderTarget = renderTarget;
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
		if (camera != null)
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
		OpenGL.setClearColor(color);
	}

	public static int getClearColor() {
		return OpenGL.getClearColor();
	}
}
