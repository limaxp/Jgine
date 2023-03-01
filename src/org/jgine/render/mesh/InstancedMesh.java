package org.jgine.render.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;

import org.jgine.utils.math.Matrix;
import org.lwjgl.system.MemoryStack;

public class InstancedMesh extends Mesh {

	public static final int MAX_SIZE = 10000;
	public static final int DATA_SIZE = Matrix.SIZE;

	protected int databo;
	protected int instanceSize;

	public InstancedMesh(int dimension, boolean hasNormals) {
		this(dimension, STATIC, hasNormals);
	}

	public InstancedMesh(int dimension, int type, boolean hasNormals) {
		super(dimension, type, hasNormals);
		databo = glGenBuffers();

		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glBufferData(GL_ARRAY_BUFFER, MAX_SIZE * DATA_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, DATA_SIZE, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 0 * Float.BYTES);
		glVertexAttribDivisor(2, 1);
		glBindVertexArray(0);
	}

	@Override
	public final void close() {
		super.close();
		glDeleteBuffers(databo);
		databo = 0;
	}

	@Override
	public final void render() {
		glBindVertexArray(vao);
		glDrawArraysInstanced(mode, 0, size, instanceSize);
		glBindVertexArray(0);
	}

	public final void setData(FloatBuffer data) {
		size = data.remaining() / DATA_SIZE;
		setData(0, data);
	}

	public final void setData(int index, FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, data);
	}

	public final FloatBuffer getData(FloatBuffer target) {
		return getData(0, target);
	}

	public final FloatBuffer getData(int index, FloatBuffer target) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glGetBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, target);
		return target;
	}

	public final void setData(int index, Matrix matrix) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = matrix.get(stack.mallocFloat(16));
			target.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, target);
		}
	}

	public final Matrix getData(int index) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(DATA_SIZE);
			glGetBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, target);
			Matrix data = new Matrix();
			data.set(target);
			return data;
		}
	}
}
