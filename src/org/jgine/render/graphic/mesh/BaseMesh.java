package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.BufferHelper;

public class BaseMesh implements AutoCloseable {

	public static final int VERTEX_SIZE = 3;
	public static final int TEXT_CORD_SIZE = 2;
	public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE;

	public final String name;
	protected int mode = GL_TRIANGLES;
	protected int vao;
	protected int vbo;
	protected int size;

	public BaseMesh(float[] vertices) {
		this("", vertices, null);
	}

	public BaseMesh(String name, float[] vertices) {
		this("", vertices, null);
	}

	public BaseMesh(float[] vertices, @Nullable float[] textureChords) {
		this("", vertices, textureChords);
	}

	public BaseMesh(String name, float[] vertices, @Nullable float[] textureChords) {
		this.name = name;
		loadData(vertices, textureChords);
	}

	@Override
	public final void close() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		vao = 0;
		vbo = 0;
	}

	public final void render() {
		glBindVertexArray(vao);
		glDrawArrays(mode, 0, size);
		glBindVertexArray(0);
	}

	protected void loadData(float[] vertices, @Nullable float[] textureChords) {
		if (textureChords == null || textureChords.length == 0)
			textureChords = BufferHelper.generateTextureChords(vertices);

		FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(3, 2, vertices, textureChords);
		loadData(verticesBuffer);
	}

	protected void loadData(FloatBuffer vertices) {
		size = vertices.remaining() / 5;
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 3 * Float.BYTES);

		glBindVertexArray(0);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
}
