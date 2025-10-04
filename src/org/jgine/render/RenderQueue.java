package org.jgine.render;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgine.render.material.Material;
import org.jgine.render.shader.Shader;
import org.jgine.utils.collection.list.IdentityArrayList;
import org.jgine.utils.math.Matrix;

/**
 * <pre>
Order:
	RenderTarget
	Program
  	ROP
  	Texture Bindings
  	Vertex Format
  	UBO Binding
  	Vertex Bindings
  	Uniform Updates
 * </pre>
 *
 */
public class RenderQueue {

	private static final Object LOCK = new Object();

	private static Map<RenderTarget, TargetData> data = new IdentityHashMap<RenderTarget, TargetData>();
	private static Map<RenderTarget, TargetData> usedData = new IdentityHashMap<RenderTarget, TargetData>();

	private static class TargetData {

		protected Map<Shader, Map<Material, List<RenderCommand>>> data = new IdentityHashMap<Shader, Map<Material, List<RenderCommand>>>();
	}

	public static void clear() {
		synchronized (LOCK) {
			Map<RenderTarget, TargetData> tmp = usedData;
			usedData = data;
			data = tmp;
			data.clear();
		}
	}

	public static void render(int vao, int mode, int numVertices, int numIndices, Matrix model, Matrix viewProjection,
			Material material, RenderTarget renderTarget, Shader shader) {
		getData(renderTarget, shader, material).add(
				new RenderCommand(vao, mode, numVertices, numIndices, model, new Matrix(model).mult(viewProjection)));
	}

	public static void render(int vao, int mode, int numVertices, int numIndices, Matrix model, Matrix viewProjection,
			Material material, RenderTarget renderTarget, Shader shader, float depth) {
		Matrix mvp = new Matrix(model).mult(viewProjection);
		mvp.m23 = depth;
		getData(renderTarget, shader, material).add(new RenderCommand(vao, mode, numVertices, numIndices, model, mvp));
	}

	public static void renderInstanced(int vao, int mode, int numVertices, int numIndices, Matrix model,
			Matrix viewProjection, Material material, RenderTarget renderTarget, Shader shader, int amount) {
		getData(renderTarget, shader, material).add(new RenderCommand(vao, mode, numVertices, numIndices, model,
				new Matrix(model).mult(viewProjection), amount));
	}

	public static void renderInstanced(int vao, int mode, int numVertices, int numIndices, Matrix model,
			Matrix viewProjection, Material material, RenderTarget renderTarget, Shader shader, int amount,
			float depth) {
		Matrix mvp = new Matrix(model).mult(viewProjection);
		mvp.m23 = depth;
		getData(renderTarget, shader, material)
				.add(new RenderCommand(vao, mode, numVertices, numIndices, model, mvp, amount));
	}

	private static List<RenderCommand> getData(RenderTarget renderTarget, Shader shader, Material material) {
		synchronized (LOCK) {
			TargetData a = data.get(renderTarget);
			if (a == null) {
				a = new TargetData();
				data.put(renderTarget, a);
			}
			Map<Material, List<RenderCommand>> b = a.data.get(shader);
			if (b == null) {
				b = new IdentityHashMap<Material, List<RenderCommand>>();
				a.data.put(shader, b);
			}
			List<RenderCommand> data = b.get(material);
			if (data == null) {
				data = Collections.synchronizedList(new IdentityArrayList<RenderCommand>());
				b.put(material, data);
			}
			return data;
		}
	}

	static void draw() {
		synchronized (LOCK) {
			Renderer.enableDepthTest();

			for (Entry<RenderTarget, TargetData> a : usedData.entrySet()) {
				RenderTarget renderTarget = a.getKey();
				if (renderTarget.isClosed())
					continue;
				renderTarget.bindViewport(RenderTarget.COLOR_ATTACHMENT0);

				for (Entry<Shader, Map<Material, List<RenderCommand>>> b : a.getValue().data.entrySet()) {
					Shader shader = b.getKey();
					shader.bind();

					for (Entry<Material, List<RenderCommand>> c : b.getValue().entrySet()) {
						Material material = c.getKey();
						material.bind(shader);

						List<RenderCommand> data = c.getValue();
						for (RenderCommand command : data) {
							shader.setTransform(command.transform, command.transformProjected);
							draw(command);
						}
					}

					shader.unbind();
				}
			}

			glBindVertexArray(0);
			RenderTarget.unbindViewport();
			Renderer.disableDepthTest();
		}
	}

	private static void draw(RenderCommand command) {
		if (command.amount == 0) {
			if (command.numIndices == 0)
				Renderer.draw(command.vao, command.mode, command.numVertices);
			else
				Renderer.drawIndexed(command.vao, command.mode, command.numIndices);
		} else {
			if (command.numIndices == 0)
				Renderer.drawInstanced(command.vao, command.mode, command.numVertices, command.amount);
			else
				Renderer.drawInstancedIndexed(command.vao, command.mode, command.numIndices, command.amount);
		}
	}

	private static class RenderCommand {

		private int vao;
		protected int mode;
		protected int numVertices;
		protected int numIndices;
		protected Matrix transform;
		protected Matrix transformProjected;
		protected int amount;

		public RenderCommand(int vao, int mode, int numVertices, int numIndices, Matrix transform,
				Matrix transformProjected) {
			this.vao = vao;
			this.mode = mode;
			this.numVertices = numVertices;
			this.numIndices = numIndices;
			this.transform = new Matrix(transform);
			this.transformProjected = new Matrix(transformProjected);
		}

		public RenderCommand(int vao, int mode, int numVertices, int numIndices, Matrix transform,
				Matrix transformProjected, int amount) {
			this(vao, mode, numVertices, numIndices, transform, transformProjected);
			this.amount = amount;
		}
	}
}
