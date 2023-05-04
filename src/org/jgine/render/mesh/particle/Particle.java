package org.jgine.render.mesh.particle;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT;
import static org.lwjgl.opengl.GL15.glBeginQuery;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glEndQuery;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGenQueries;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL15.glGetQueryObjectui;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_RASTERIZER_DISCARD;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN;
import static org.lwjgl.opengl.GL30.glBeginTransformFeedback;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glEndTransformFeedback;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.jgine.core.Transform;
import org.jgine.render.Renderer;
import org.lwjgl.system.MemoryStack;

public class Particle {

	public static final int MAX_SIZE = 10000;
	public static final int DATA_SIZE = 4; // x,y,z,type

	protected int[] vao;
	protected int[] vbo;
	protected int query;
	protected int currentReadBuffer;
	protected int instanceSize;
	protected int numToGenerate;

	public Particle() {
		vao = new int[2];
		vbo = new int[2];
		query = glGenQueries();

		for (int i = 0; i < 2; i++) {
			vao[i] = glGenVertexArrays();
			vbo[i] = glGenBuffers();
			glBindVertexArray(vao[i]);
			glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
			glBufferData(GL_ARRAY_BUFFER, MAX_SIZE * DATA_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 0 * Float.BYTES);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 1, GL_INT, false, DATA_SIZE * Float.BYTES, 3 * Float.BYTES);
		}
		glBindVertexArray(0);
		instanceSize = 1;
	}

	public void close() {
		for (int i = 0; i < 2; i++) {
			glDeleteVertexArrays(vao[i]);
			glDeleteBuffers(vbo[i]);
			vao[i] = 0;
			vbo[i] = 0;
		}
	}

	public void update(Transform transform) {
		Renderer.PARTICLE_CALC_SHADER.setParticle(transform, this);

		glEnable(GL_RASTERIZER_DISCARD);
		glBindVertexArray(vao[currentReadBuffer]);
		glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo[1 - currentReadBuffer]);
		glBeginQuery(GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN, query);
		glBeginTransformFeedback(GL_POINTS);
		glDrawArrays(GL_POINTS, 0, instanceSize);
		glEndTransformFeedback();
		glEndQuery(GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN);
		glBindVertexArray(0);
		glDisable(GL_RASTERIZER_DISCARD);

		instanceSize = glGetQueryObjectui(query, GL_QUERY_RESULT);
//		System.out.println("instances: " + instanceSize);
		currentReadBuffer = 1 - currentReadBuffer;

//		getData();
	}

	public final float[] getData() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo[currentReadBuffer]);
		float[] result = new float[instanceSize * DATA_SIZE];
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(instanceSize * DATA_SIZE);
			glGetBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
			int i = 0;
			while (buffer.hasRemaining()) {
				result[i++] = buffer.get();
				System.out.println(result[i - 1]);
			}
			return result;
		}
	}

	public int getVao() {
		return vao[currentReadBuffer];
	}

	public final int getVbo() {
		return vbo[currentReadBuffer];
	}

	public int getInstanceSize() {
		return instanceSize;
	}

	public void setNumToGenerate(int numToGenerate) {
		this.numToGenerate = numToGenerate;
	}

	public int getNumToGenerate() {
		return numToGenerate;
	}

}
