package org.jgine.render.mesh.particle;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
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
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;

import org.jgine.render.mesh.BaseMesh;
import org.lwjgl.system.MemoryStack;

import maxLibs.utils.Color;

public class BillboardParticle extends BaseMesh {

	public static final int MAX_SIZE = 10000;
	public static final int DATA_SIZE = 8; // x,y,z,size,r,g,b,a

	protected int databo;
	protected int instanceSize;

	public BillboardParticle() {
		super(2, false);
		mode = GL_TRIANGLE_STRIP;
		loadVertices(new float[] { -1, 1, 1, 1, -1, -1, 1, -1 }, new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		databo = glGenBuffers();

		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glBufferData(GL_ARRAY_BUFFER, MAX_SIZE * DATA_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 4, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 0 * Float.BYTES);
		glVertexAttribDivisor(2, 1);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 4, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 4 * Float.BYTES);
		glVertexAttribDivisor(3, 1);
		glBindVertexArray(0);
	}

	@Override
	public final void close() {
		super.close();
		glDeleteBuffers(databo);
		databo = 0;
	}

	public final void setData(FloatBuffer data) {
		instanceSize = data.remaining() / DATA_SIZE;
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

	public final void setData(double[] vec3f, float size, int color) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(vec3f.length / 3 * DATA_SIZE);
			for (int i = 0; i < vec3f.length; i += 3) {
				buffer.put((float) vec3f[i]);
				buffer.put((float) vec3f[i + 1]);
				buffer.put((float) vec3f[i + 2]);
				buffer.put(size);
				Color.toRGBABuffer(buffer, color);
			}
			buffer.flip();
			setData(buffer);
		}
	}

	public final void setData(int index, ParticleData data) {
		setData(index, data.x, data.y, data.z, data.size, data.color);
	}

	public final void setData(int index, float x, float y, float z, float size, int color) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
			buffer.put(x);
			buffer.put(y);
			buffer.put(z);
			buffer.put(size);
			Color.toRGBABuffer(buffer, color);
			buffer.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
		}
	}

	public final ParticleData getData(int index) {
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
			glGetBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
			ParticleData data = new ParticleData();
			data.x = buffer.get();
			data.y = buffer.get();
			data.z = buffer.get();
			data.size = buffer.get();
			data.color = Color.rgba(buffer.get(), buffer.get(), buffer.get(), buffer.get());
			return data;
		}
	}

	public int getInstanceSize() {
		return instanceSize;
	}

	public static class ParticleData {

		public float x;
		public float y;
		public float z;
		public float size;
		public int color;
	}
}
