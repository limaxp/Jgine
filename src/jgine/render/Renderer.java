package jgine.render;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.RenderTarget.Attachment;
import jgine.render.light.PointLight;
import jgine.render.material.Material;
import jgine.render.material.Texture;
import jgine.render.mesh.BaseMesh;
import jgine.render.mesh.Mesh;
import jgine.render.mesh.MeshGenerator;
import jgine.render.mesh.Model;
import jgine.render.mesh.ParticleMesh;
import jgine.render.mesh.TileMapMesh;
import jgine.render.mesh.TileMapMesh.TileMapMeshLayer;
import jgine.render.shader.BasicShader;
import jgine.render.shader.ParticleCalcShader;
import jgine.render.shader.Phong2dShader;
import jgine.render.shader.PhongShader;
import jgine.render.shader.PostProcessShader;
import jgine.render.shader.Shader;
import jgine.render.shader.TextShader;
import jgine.render.shader.TextureShader;
import jgine.render.shader.TileMapShader;
import jgine.system.camera.Camera;
import jgine.system.collision.collider.AxisAlignedBoundingQuad;
import jgine.system.collision.collider.CircleCollider;
import jgine.system.light.LightScene;
import jgine.utils.Options;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.math.vector.Vector4f;

public class Renderer {

	protected static final Matrix UI_MATRIX = Matrix.asOrthographic(-1, 1, -1, 1, -1, 1);

	public static final BasicShader BASIC_SHADER;
	public static final TextureShader TEXTURE_SHADER;
	public static final TextShader TEXT_SHADER;
	public static final PhongShader PHONG_SHADER;
	public static final Phong2dShader PHONG_2D_SHADER;
	public static final TextureShader BILLBOARD_SHADER;
	public static final ParticleCalcShader PARTICLE_CALC_SHADER;
	public static final TextureShader PARTICLE_DRAW_SHADER;
	public static final TileMapShader TILE_MAP_SHADER;
	public static final PostProcessShader POST_PROCESS_SHADER;

	protected static final RenderTarget POST_PROCESS_TARGET;
	protected static final BaseMesh QUAD_MESH;
	protected static final Mesh CUBE_MESH;

	protected static Camera camera;
	protected static RenderTarget renderTarget;
	protected static Matrix projectionMatrix;
	protected static Shader shader;
	private static final List<Mesh> TEMP_MESHES = Collections.synchronizedList(new UnorderedIdentityArrayList<Mesh>());
	private static int drawCalls;

	static {
		BASIC_SHADER = new BasicShader(ResourceManager.getShader("BasicVertex"), null,
				ResourceManager.getShader("BasicFragment"));
		TEXTURE_SHADER = new TextureShader(ResourceManager.getShader("TextureVertex"), null,
				ResourceManager.getShader("TextureFragment"));
		TEXT_SHADER = new TextShader(ResourceManager.getShader("TextVertex"), null,
				ResourceManager.getShader("TextFragment"));
		PHONG_SHADER = new PhongShader(ResourceManager.getShader("PhongVertex"), null,
				ResourceManager.getShader("PhongFragment"));
		PHONG_2D_SHADER = new Phong2dShader(ResourceManager.getShader("Phong2dVertex"), null,
				ResourceManager.getShader("Phong2dFragment"));
		BILLBOARD_SHADER = new TextureShader(ResourceManager.getShader("BillboardVertex"), null,
				ResourceManager.getShader("BillboardFragment"));
		PARTICLE_CALC_SHADER = new ParticleCalcShader(ResourceManager.getShader("ParticleCalcVertex"),
				ResourceManager.getShader("ParticleCalcGeometry"), null);
		PARTICLE_DRAW_SHADER = new TextureShader(ResourceManager.getShader("ParticleDrawVertex"),
				ResourceManager.getShader("ParticleDrawGeometry"), ResourceManager.getShader("ParticleDrawFragment"));
		TILE_MAP_SHADER = new TileMapShader(ResourceManager.getShader("TileMapVertex"), null,
				ResourceManager.getShader("TileMapFragment"));
		POST_PROCESS_SHADER = new PostProcessShader(ResourceManager.getShader("PostProcessVertex"), null,
				ResourceManager.getShader("PostProcessFragment"));

		POST_PROCESS_TARGET = new RenderTarget();
		POST_PROCESS_TARGET.bind();
		POST_PROCESS_TARGET.setTexture(Texture.RGB, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.asInt(),
				Options.RESOLUTION_Y.asInt());
		POST_PROCESS_TARGET.checkStatus();
		RenderTarget.unbind();

		QUAD_MESH = MeshGenerator.quad(1.0f);
		CUBE_MESH = MeshGenerator.cube(1.0f);
	}

	public static void terminate() {
		QUAD_MESH.close();
		CUBE_MESH.close();
		CircleCollider.COLLIDER_MESH.close();
		AxisAlignedBoundingQuad.COLLIDER_MESH.close();
	}

	public static void update(float dt) {
		RenderQueue.clear();
		for (Mesh mesh : TEMP_MESHES)
			mesh.close();
		TEMP_MESHES.clear();
		drawCalls = 0;
		POST_PROCESS_SHADER.update(dt);
	}

	public static void draw(List<RenderConfiguration> renderConfigs) {
		RenderQueue.draw();
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
		setShader(POST_PROCESS_SHADER);
		UIRenderer.renderQuad(UI_MATRIX, new Material(POST_PROCESS_TARGET.getTexture(RenderTarget.COLOR_ATTACHMENT0)));
	}

	public static void setLights(LightScene lightScene) {
		Vector4f ambientLight = Vector4f.fromColor(lightScene.getAmbientLight());
		List<PointLight> pointLights = lightScene.getPointLights();
		PHONG_SHADER.bind();
		PHONG_SHADER.setAmbientLight(ambientLight);
		PHONG_SHADER.setPointLights(pointLights);
		PHONG_SHADER.setDirectionalLight(lightScene.getDirectionalLight());

		PHONG_2D_SHADER.bind();
		PHONG_2D_SHADER.setAmbientLight(ambientLight);
		PHONG_2D_SHADER.setPointLights(pointLights);

		TILE_MAP_SHADER.bind();
		TILE_MAP_SHADER.setAmbientLight(ambientLight);
		TILE_MAP_SHADER.setPointLights(pointLights);
	}

	public static void render(Matrix transform, Model model) {
		Mesh[] meshes = model.getMeshes();
		Material[] materials = model.getMaterials();
		for (int i = 0; i < meshes.length; i++)
			render(transform, meshes[i], materials[i]);
	}

	public static void render(Matrix transform, Mesh mesh, Material material) {
		Matrix mvp = new Matrix(transform).mult(projectionMatrix);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		drawIndexed(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, BaseMesh mesh, Material material) {
		Matrix mvp = new Matrix(transform).mult(projectionMatrix);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		draw(mesh.getVao(), mesh.mode, mesh.getSize());
	}

	public static void render(Matrix transform, TileMapMesh tileMap, Material material) {
		Matrix mvp = new Matrix(transform).mult(projectionMatrix);
		material.bind(shader);
		shader.setTransform(transform, mvp);
		int amount = tileMap.getTileswidth() * tileMap.getTilesheight();
		for (int i = 0; i < tileMap.getLayerSize(); i++) {
			TileMapMeshLayer layer = tileMap.getLayer(i);
			drawInstanced(layer.getVao(), layer.mode, layer.getSize(), amount);
		}
	}

	public static void render(Matrix transform, ParticleMesh particle, Material material) {
		Matrix mvp = new Matrix(transform).mult(projectionMatrix);
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

	public static void draw(int vao, int mode, int numVertices) {
		OpenGL.bindVertexArray(vao);
		OpenGL.drawArrays(mode, 0, numVertices);
		drawCalls++;
	}

	public static void drawIndexed(int vao, int mode, int numIndices) {
		OpenGL.bindVertexArray(vao);
		OpenGL.drawElements(mode, numIndices, GL_UNSIGNED_INT, 0);
		drawCalls++;
	}

	public static void drawInstanced(int vao, int mode, int numVertices, int amount) {
		OpenGL.bindVertexArray(vao);
		OpenGL.drawArraysInstanced(mode, 0, numVertices, amount);
		drawCalls++;
	}

	public static void drawInstancedIndexed(int vao, int mode, int numIndices, int amount) {
		OpenGL.bindVertexArray(vao);
		OpenGL.drawElementsInstanced(mode, numIndices, GL_UNSIGNED_INT, 0, amount);
		drawCalls++;
	}

	protected static void deleteTempMesh(Mesh mesh) {
		TEMP_MESHES.add(mesh);
	}

	public static int getDrawCalls() {
		return drawCalls;
	}

	public static void setCamera(Camera camera) {
		Renderer.camera = camera;
		setRenderTarget(camera.getRenderTarget());
		projectionMatrix = camera.getViewProjection();
	}

	public static Camera getCamera() {
		return camera;
	}

	public static void setRenderTarget(@Nullable RenderTarget renderTarget) {
		if (Renderer.renderTarget != renderTarget) {
			if (renderTarget == null)
				RenderTarget.unbindViewport();
			else
				renderTarget.bindViewport(RenderTarget.COLOR_ATTACHMENT0);
			Renderer.renderTarget = renderTarget;
		}
	}

	@Nullable
	public static RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public static void setShader(Shader shader) {
		if (Renderer.shader != shader) {
			if (Renderer.shader != null)
				Renderer.shader.unbind();
			Renderer.shader = shader;
			shader.bind();
			shader.setCamera(camera);
		}
	}

	public static Shader getShader() {
		return shader;
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

	public static void screenShake(float time) {
		POST_PROCESS_SHADER.shake(time);
	}

	public static void screenShake(float time, float strength) {
		POST_PROCESS_SHADER.shake(time, strength);
	}
}
