package org.jgine.render;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.Mesh;
import org.jgine.render.shader.Shader;
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

	private static Map<RenderTarget, Map<Shader, Map<Material, RenderData>>> data = new IdentityHashMap<RenderTarget, Map<Shader, Map<Material, RenderData>>>();
	private static Map<RenderTarget, Map<Shader, Map<Material, RenderData>>> usedData = new IdentityHashMap<RenderTarget, Map<Shader, Map<Material, RenderData>>>();
	private static final List<Mesh> TEMP_MESHES = Collections.synchronizedList(new UnorderedIdentityArrayList<Mesh>());
	private static int drawCallAmount;

	public static void clear() {
		synchronized (LOCK) {
			Map<RenderTarget, Map<Shader, Map<Material, RenderData>>> tmp = usedData;
			usedData = data;
			data = tmp;
			data.clear();
			for (Mesh mesh : TEMP_MESHES)
				mesh.close();
			TEMP_MESHES.clear();
		}
	}

	public static void render(int vao, int mode, int numVertices, int numIndices, Matrix model, Matrix viewProjection,
			Material material, RenderTarget renderTarget, Shader shader) {
		getData(renderTarget, shader, material).commands.add(
				new RenderCommand(vao, mode, numVertices, numIndices, model, new Matrix(model).mult(viewProjection)));
	}

	public static void render(int vao, int mode, int numVertices, int numIndices, Matrix model, Matrix viewProjection,
			Material material, RenderTarget renderTarget, Shader shader, float depth) {
		Matrix mvp = new Matrix(model).mult(viewProjection);
		mvp.m23 = depth;
		getData(renderTarget, shader, material).commands
				.add(new RenderCommand(vao, mode, numVertices, numIndices, model, mvp));
	}

	public static void renderInstanced(int vao, int mode, int numVertices, int numIndices, Matrix model,
			Matrix viewProjection, Material material, RenderTarget renderTarget, Shader shader, int amount) {
		getData(renderTarget, shader, material).commandsInstanced.add(new RenderInstancedCommand(vao, mode, numVertices,
				numIndices, model, new Matrix(model).mult(viewProjection), amount));
	}

	public static void renderInstanced(int vao, int mode, int numVertices, int numIndices, Matrix model,
			Matrix viewProjection, Material material, RenderTarget renderTarget, Shader shader, int amount,
			float depth) {
		Matrix mvp = new Matrix(model).mult(viewProjection);
		mvp.m23 = depth;
		getData(renderTarget, shader, material).commandsInstanced
				.add(new RenderInstancedCommand(vao, mode, numVertices, numIndices, model, mvp, amount));
	}

	private static RenderData getData(RenderTarget renderTarget, Shader shader, Material material) {
		synchronized (LOCK) {
			Map<Shader, Map<Material, RenderData>> a = data.get(renderTarget);
			if (a == null) {
				a = new IdentityHashMap<Shader, Map<Material, RenderData>>();
				data.put(renderTarget, a);
			}
			Map<Material, RenderData> b = a.get(shader);
			if (b == null) {
				b = new IdentityHashMap<Material, RenderData>();
				a.put(shader, b);
			}
			RenderData data = b.get(material);
			if (data == null) {
				data = new RenderData();
				b.put(material, data);
			}
			return data;
		}
	}

	public static void draw() {
		synchronized (LOCK) {
			drawCallAmount = 0;
			Renderer.enableDepthTest();

			for (Entry<RenderTarget, Map<Shader, Map<Material, RenderData>>> a : usedData.entrySet()) {
				RenderTarget renderTarget = a.getKey();
				if (renderTarget.isClosed())
					continue;
				renderTarget.bindViewport(RenderTarget.COLOR_ATTACHMENT0);
				renderTarget.clear();

				for (Entry<Shader, Map<Material, RenderData>> b : a.getValue().entrySet()) {
					Shader shader = b.getKey();
					shader.bind();

					for (Entry<Material, RenderData> c : b.getValue().entrySet()) {
						Material material = c.getKey();
						material.bind(shader);

						RenderData data = c.getValue();
						drawCallAmount += data.commands.size() + data.commandsInstanced.size();
						for (RenderCommand command : data.commands) {
							shader.setTransform(command.transform, command.transformProjected);
							render(command.vao, command.mode, command.numVertices, command.numIndices);
						}

						for (RenderInstancedCommand command : data.commandsInstanced) {
							shader.setTransform(command.transform, command.transformProjected);
							renderInstanced(command.vao, command.mode, command.numVertices, command.numIndices,
									command.amount);
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

	public static void render(int vao, int mode, int numVertices, int numIndices) {
		glBindVertexArray(vao);
		if (numIndices == 0)
			glDrawArrays(mode, 0, numVertices);
		else
			glDrawElements(mode, numIndices, GL_UNSIGNED_INT, 0);
	}

	public static void renderInstanced(int vao, int mode, int numVertices, int numIndices, int amount) {
		glBindVertexArray(vao);
		if (numIndices == 0)
			glDrawArraysInstanced(mode, 0, numVertices, amount);
		else
			glDrawElementsInstanced(mode, numIndices, GL_UNSIGNED_INT, 0, amount);
	}

	public static void deleteTempMesh(Mesh mesh) {
		TEMP_MESHES.add(mesh);
	}

	public static int getDrawCallAmount() {
		return drawCallAmount;
	}

	private static class RenderData {

		protected List<RenderCommand> commands = Collections.synchronizedList(new IdentityArrayList<RenderCommand>());
		protected List<RenderInstancedCommand> commandsInstanced = Collections
				.synchronizedList(new IdentityArrayList<RenderInstancedCommand>());
	}

	private static class RenderCommand {

		protected int vao;
		protected int mode;
		protected int numVertices;
		protected int numIndices;
		protected Matrix transform;
		protected Matrix transformProjected;

		protected RenderCommand(int vao, int mode, int numVertices, int numIndices, Matrix transform,
				Matrix transformProjected) {
			this.vao = vao;
			this.mode = mode;
			this.numVertices = numVertices;
			this.numIndices = numIndices;
			this.transform = transform;
			this.transformProjected = transformProjected;
		}
	}

	private static class RenderInstancedCommand extends RenderCommand {

		protected int amount;

		protected RenderInstancedCommand(int vao, int mode, int numVertices, int numIndices, Matrix transform,
				Matrix transformProjected, int amount) {
			super(vao, mode, numVertices, numIndices, transform, transformProjected);
			this.amount = amount;
		}
	}
}
