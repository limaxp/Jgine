package org.jgine.render.shader;

import static org.lwjgl.opengl.GL42.GL_ALL_BARRIER_BITS;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_BUFFER_UPDATE_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_COMMAND_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_ELEMENT_ARRAY_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_FRAMEBUFFER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_PIXEL_BUFFER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_TEXTURE_FETCH_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_TEXTURE_UPDATE_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_TRANSFORM_FEEDBACK_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_UNIFORM_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Material;
import org.jgine.utils.math.Matrix;

public class ComputeShader extends Shader {

	public static final int COMPUTE_SHADER = GL_COMPUTE_SHADER;

	// Some barriers, included here for convenience

	public static final int VERTEX_ATTRIB_ARRAY_BARRIER_BIT = GL_VERTEX_ATTRIB_ARRAY_BARRIER_BIT;
	public static final int ELEMENT_ARRAY_BARRIER_BIT = GL_ELEMENT_ARRAY_BARRIER_BIT;
	public static final int UNIFORM_BARRIER_BIT = GL_UNIFORM_BARRIER_BIT;
	public static final int TEXTURE_FETCH_BARRIER_BIT = GL_TEXTURE_FETCH_BARRIER_BIT;
	public static final int SHADER_IMAGE_ACCESS_BARRIER_BIT = GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
	public static final int COMMAND_BARRIER_BIT = GL_COMMAND_BARRIER_BIT;
	public static final int PIXEL_BUFFER_BARRIER_BIT = GL_PIXEL_BUFFER_BARRIER_BIT;
	public static final int TEXTURE_UPDATE_BARRIER_BIT = GL_TEXTURE_UPDATE_BARRIER_BIT;
	public static final int BUFFER_UPDATE_BARRIER_BIT = GL_BUFFER_UPDATE_BARRIER_BIT;
	public static final int FRAMEBUFFER_BARRIER_BIT = GL_FRAMEBUFFER_BARRIER_BIT;
	public static final int TRANSFORM_FEEDBACK_BARRIER_BIT = GL_TRANSFORM_FEEDBACK_BARRIER_BIT;
	public static final int ATOMIC_COUNTER_BARRIER_BIT = GL_ATOMIC_COUNTER_BARRIER_BIT;
	public static final int SHADER_STORAGE_BARRIER_BIT = GL_SHADER_STORAGE_BARRIER_BIT;
	public static final int ALL_BARRIER_BITS = GL_ALL_BARRIER_BITS;

	public ComputeShader(int program) {
		super(program);
	}

	public ComputeShader() {
		super();
	}

	public ComputeShader(@Nullable String compute) {
		super();
		compile(COMPUTE_SHADER, compute);
		link();
	}

	public void run(int numX, int numY, int numZ) {
		glDispatchCompute(numX, numY, numZ);
	}

	public void barrier(int barriers) {
		glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
	}

	@Override
	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
	}

	@Override
	public void setMaterial(Material material) {
	}
}
