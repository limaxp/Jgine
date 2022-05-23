package org.jgine.render.graphic.particle;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.BufferHelper;
import org.lwjgl.BufferUtils;

public class BillboardParticle implements AutoCloseable {

	public static final int MAX_PARTICLES = 1000;

	public static final Consumer<BillboardParticle> NULL_ANIMATION = (particle) -> {};

	public static final int VERTEX_SIZE = 2;
	public static final int TEXT_CORD_SIZE = 2;
	public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE;
	public static final int POSITION_SIZE = 4; // x,y,z,size

	protected static final int vbo;

	static {
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, BufferHelper.createFloatBuffer(2, 2, new float[] { -1, 1, 1, 1, -1, -1, 1, -1 },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 }), GL_STATIC_DRAW);
	}

	public static void free() {
		glDeleteBuffers(vbo);
	}

	protected int mode = GL_TRIANGLE_STRIP;
	protected int vao;
	protected int posbo;
	protected FloatBuffer posBuffer;
	protected int size;
	protected Consumer<BillboardParticle> animation = NULL_ANIMATION;

	public BillboardParticle() {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 2 * Float.BYTES);

		posbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, posbo);
		int posBufferSize = MAX_PARTICLES * POSITION_SIZE * Float.BYTES;
		posBuffer = BufferUtils.createFloatBuffer(posBufferSize);
		glBufferData(GL_ARRAY_BUFFER, posBufferSize, GL_STREAM_DRAW);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, POSITION_SIZE, GL_FLOAT, false, POSITION_SIZE * Float.BYTES, 0 * Float.BYTES);
		glVertexAttribDivisor(2, 1);

		glBindVertexArray(0);
	}

	@Override
	public final void close() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(posbo);
		vao = 0;
		posbo = 0;
	}

	public final void render() {
		glBindVertexArray(vao);
		glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, size);
		glBindVertexArray(0);
	}

	public final boolean update() {
		animation.accept(this);
		return true;
	}

	public final void setPositions(float[] positions) {
		posBuffer.position(0);
		posBuffer.put(positions);
		posBuffer.flip();
		updatePositions();
	}

	public final void setPositions(FloatBuffer positions) {
		posBuffer.position(0);
		posBuffer.put(positions);
		posBuffer.flip();
		updatePositions();
	}

	public final void setPositions(double[] vec3f, float size) {
		posBuffer.position(0);
		for (int i = 0; i < vec3f.length; i += 3) {
			posBuffer.put((float) vec3f[i]);
			posBuffer.put((float) vec3f[i + 1]);
			posBuffer.put((float) vec3f[i + 2]);
			posBuffer.put(size);
		}
		posBuffer.flip();
		updatePositions();
	}

	protected final void updatePositions() {
		size = posBuffer.remaining() / POSITION_SIZE;
		glBindBuffer(GL_ARRAY_BUFFER, posbo);
		glBufferData(GL_ARRAY_BUFFER, MAX_PARTICLES * POSITION_SIZE * Float.BYTES, GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, posBuffer);
	}

	public final void setPosition(int index, float position) {
		posBuffer.put(index, position);
	}

	public final float getPosition(int index) {
		return posBuffer.get(index);
	}

	public final FloatBuffer getPositionBuffer() {
		return posBuffer;
	}

	public void setAnimation(@Nullable Consumer<BillboardParticle> animation) {
		this.animation = animation == null ? NULL_ANIMATION : animation;
	}

	public Consumer<BillboardParticle> getAnimation() {
		return animation;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
}
