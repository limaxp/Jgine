package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.memory.BufferHelper;
import org.lwjgl.BufferUtils;

public class Mesh2D implements AutoCloseable {

	public static final int VERTEX_SIZE = 2;
	public static final int TEXT_CORD_SIZE = 2;
	public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE;

	public final String name;
	protected int mode = GL_TRIANGLES;
	protected int vao;
	protected int vbo;
	protected int ibo;
	protected int size;

	public Mesh2D(float[] vertices, int[] indices) {
		this("", vertices, indices, null);
	}

	public Mesh2D(String name, float[] vertices, int[] indices) {
		this("", vertices, indices, null);
	}

	public Mesh2D(float[] vertices, int[] indices, @Nullable float[] textureChords) {
		this("", vertices, indices, textureChords);
	}

	public Mesh2D(String name, float[] vertices, int[] indices, @Nullable float[] textureChords) {
		this.name = name;
		loadData(vertices, indices, textureChords);
	}

	@Override
	public final void close() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
	}

	public final void render() {
		glBindVertexArray(vao);
		glDrawElements(mode, size, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	protected void loadData(float[] vertices, int[] indices, @Nullable float[] textureChords) {
		if (textureChords == null || textureChords.length == 0)
			textureChords = BufferHelper.generateTextureChords(vertices);

		FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(2, 2, vertices, textureChords);
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();

		loadData(verticesBuffer, indicesBuffer);
	}

	protected void loadData(FloatBuffer vertices, IntBuffer indices) {
		size = indices.remaining();
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 2 * Float.BYTES);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		glBindVertexArray(0);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
}
