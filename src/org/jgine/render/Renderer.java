package org.jgine.render;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.RenderTarget.Attachment;
import org.jgine.render.graphic.TileMap;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.jgine.render.graphic.mesh.Mesh;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.render.graphic.mesh.ModelGenerator;
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

	public static final BasicShader BASIC_SHADER;
	public static final TextureShader TEXTURE_SHADER;
	public static final PhongShader PHONG_SHADER;
	public static final BillboardParticleShader PARTICLE_SHADER;
	public static final TileMapShader TILE_MAP_SHADER;
	public static final CircleShader CIRCLE_SHADER;
	public static final PostProcessShader POST_PROCESS_SHADER;

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
		PARTICLE_SHADER = new BillboardParticleShader("BillboardParticle");
		TILE_MAP_SHADER = new TileMapShader("TileMap");
		CIRCLE_SHADER = new CircleShader("Circle");
		POST_PROCESS_SHADER = new PostProcessShader("PostProcess");

		POST_PROCESS_TARGET = new RenderTarget();
		POST_PROCESS_TARGET.bind();
		POST_PROCESS_TARGET.setTexture(Texture.RGB, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt());
		POST_PROCESS_TARGET.checkStatus();
		POST_PROCESS_TARGET.unbind();

		QUAD_MESH = ModelGenerator.quad(1.0f);
		CUBE_MESH = ModelGenerator.cube(1.0f);
	}

	public static void init() {
	}

	public static void terminate() {
		QUAD_MESH.close();
		CUBE_MESH.close();
		BillboardParticle.free();

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
			lineMesh.loadData(3, new float[] { start.x, start.y, start.z, end.x, end.y, end.z });
			lineMesh.mode = Mesh.LINES;
			lineMesh.render();
		}
	}

	public static void renderLine3d(Matrix transform, float[] points, Material material, boolean loop) {
		shader.setTransform(transform, new Matrix(transform).mult(camera.getMatrix()));
		material.bind(shader);
		try (BaseMesh lineMesh = new BaseMesh()) {
			lineMesh.loadData(3, points);
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
			lineMesh.loadData(2, points);
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
		setRenderTarget(camera.getRenderTarget());
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
