package org.jgine.render.graphic.particle;

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
import java.nio.IntBuffer;

import org.jgine.render.graphic.mesh.Mesh;
import org.lwjgl.system.MemoryStack;

public class InstancedMesh extends Mesh {

	public static final int MAX_SIZE = 10000;

	public static final int TEXT_CORD_SIZE = 2;
	public static final int DATA_SIZE = 4; // x,y,z,size

	protected int databo;
	protected int instanceSize;

	public InstancedMesh() {
		this(STATIC);
	}

	public InstancedMesh(int type) {
		super(type);
		databo = glGenBuffers();
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

	protected void initDataBuffer() {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glBufferData(GL_ARRAY_BUFFER, MAX_SIZE * DATA_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
		
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, DATA_SIZE, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 0 * Float.BYTES);
		glVertexAttribDivisor(2, 1);
		glBindVertexArray(0);
	}

	@Override
	public void loadDataNoNormals(int dimension, FloatBuffer vertices) {
		super.loadDataNoNormals(dimension, vertices);
		initDataBuffer();
	}

	@Override
	public void loadData(int dimension, FloatBuffer vertices) {
		super.loadData(dimension, vertices);
		initDataBuffer();
	}

	@Override
	public void loadDataNoNormals(int dimension, FloatBuffer vertices, IntBuffer indices) {
		super.loadDataNoNormals(dimension, vertices, indices);
		initDataBuffer();
	}

	@Override
	public void loadData(int dimension, FloatBuffer vertices, IntBuffer indices) {
		super.loadData(dimension, vertices, indices);
		initDataBuffer();
	}

	public final void setData(int index, InstanceData data) {
		setData(index, data.x, data.y, data.z, data.size);
	}

	public final void setData(int index, float x, float y, float z, float size) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
			buffer.put(x);
			buffer.put(y);
			buffer.put(z);
			buffer.put(size);
			buffer.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
		}
	}

	public final InstanceData getData(int index) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
			glGetBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
			InstanceData data = new InstanceData();
			data.x = buffer.get();
			data.y = buffer.get();
			data.z = buffer.get();
			data.size = buffer.get();
			return data;
		}
	}

	public final void setData(FloatBuffer data) {
		setData(0, data);
	}

	public final void setData(int index, FloatBuffer data) {
		size = data.remaining() / DATA_SIZE;
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

	public static class InstanceData {

		public float x;
		public float y;
		public float z;
		public float size;
	}
}
