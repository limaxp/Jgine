package org.jgine.render.shader;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform2iv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform3iv;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniform4iv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glUniform1ui;
import static org.lwjgl.opengl.GL30.glUniform2ui;
import static org.lwjgl.opengl.GL30.glUniform2uiv;
import static org.lwjgl.opengl.GL30.glUniform3ui;
import static org.lwjgl.opengl.GL30.glUniform3uiv;
import static org.lwjgl.opengl.GL30.glUniform4ui;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.rotation.Quaternionf;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector3i;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.math.vector.Vector4i;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.graphic.material.Material;
import org.lwjgl.system.MemoryStack;

public abstract class Shader {

	public static final Shader NULL = new Shader() {

		@Override
		public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		}

		@Override
		public void setMaterial(Material material) {
		}
	};

	public final String name;
	private int program;
	private int vertexShader;
	private int geometryShader;
	private int fragmentShader;

	private Shader() {
		name = "";
	}

	public Shader(String name) {
		this.name = name;
		program = glCreateProgram();
		if (program == 0) {
			Logger.err("Shader: Creation failed!");
			System.exit(1);
		}
		setVertexShader(ResourceManager.getShader(name + "Vertex"));
		setFragmentShader(ResourceManager.getShader(name + "Fragment"));
		compileShader();
	}

	public final void delete() {
		glDeleteShader(vertexShader);
		glDeleteShader(geometryShader);
		glDeleteShader(fragmentShader);
		glDeleteProgram(program);
	}

	public abstract void setTransform(Matrix matrix, Matrix projectionMatrix);

	public abstract void setMaterial(Material material);

	public void bind() {
		glUseProgram(program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public final int[] addUniforms(String uniform, int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++)
			result[i] = addUniform(uniform + '[' + i + ']');
		return result;
	}

	public final int addUniform(String uniform) {
		int uniformLoc = glGetUniformLocation(program, uniform);
		if (uniformLoc == -1) {
			Logger.err("Shader: Uniform '" + uniform + "' adding failed!");
			System.exit(1);
		}
		return uniformLoc;
	}

	public final void setUniformi(int uniform, int i) {
		glUniform1i(uniform, i);
	}

	public final void setUniformi(int uniform, int[] arr) {
		glUniform1iv(uniform, arr);
	}

	public final void setUniformi(int uniform, IntBuffer buffer) {
		glUniform1iv(uniform, buffer);
	}

	public final void setUniform2i(int uniform, Vector2i vector) {
		glUniform2i(uniform, vector.x, vector.y);
	}

	public final void setUniform2i(int uniform, int i1, int i2) {
		glUniform2i(uniform, i1, i2);
	}

	public final void setUniform2i(int uniform, int[] arr) {
		glUniform2iv(uniform, arr);
	}

	public final void setUniform2i(int uniform, IntBuffer buffer) {
		glUniform2iv(uniform, buffer);
	}

	public final void setUniform3i(int uniform, Vector3i vector) {
		glUniform3i(uniform, vector.x, vector.y, vector.z);
	}

	public final void setUniform3i(int uniform, int i1, int i2, int i3) {
		glUniform3i(uniform, i1, i2, i3);
	}

	public final void setUniform3i(int uniform, int[] arr) {
		glUniform3iv(uniform, arr);
	}

	public final void setUniform3i(int uniform, IntBuffer buffer) {
		glUniform3iv(uniform, buffer);
	}

	public final void setUniform4i(int uniform, Vector4i vector) {
		glUniform4i(uniform, vector.x, vector.y, vector.z, vector.w);
	}

	public final void setUniform4i(int uniform, int i1, int i2, int i3, int i4) {
		glUniform4i(uniform, i1, i2, i3, i4);
	}

	public final void setUniform4i(int uniform, int[] arr) {
		glUniform4iv(uniform, arr);
	}

	public final void setUniform4i(int uniform, IntBuffer buffer) {
		glUniform4iv(uniform, buffer);
	}

	public final void setUniformui(int uniform, int i) {
		glUniform1ui(uniform, i);
	}

	public final void setUniformui(int uniform, int[] arr) {
		glUniform1uiv(uniform, arr);
	}

	public final void setUniformui(int uniform, IntBuffer buffer) {
		glUniform1uiv(uniform, buffer);
	}

	public final void setUniform2ui(int uniform, Vector2i vector) {
		glUniform2ui(uniform, vector.x, vector.y);
	}

	public final void setUniform2ui(int uniform, int i1, int i2) {
		glUniform2ui(uniform, i1, i2);
	}

	public final void setUniform2ui(int uniform, int[] arr) {
		glUniform2uiv(uniform, arr);
	}

	public final void setUniform2ui(int uniform, IntBuffer buffer) {
		glUniform2uiv(uniform, buffer);
	}

	public final void setUniform3ui(int uniform, Vector3i vector) {
		glUniform3ui(uniform, vector.x, vector.y, vector.z);
	}

	public final void setUniform3ui(int uniform, int i1, int i2, int i3) {
		glUniform3ui(uniform, i1, i2, i3);
	}

	public final void setUniform3ui(int uniform, int[] arr) {
		glUniform3uiv(uniform, arr);
	}

	public final void setUniform3ui(int uniform, IntBuffer buffer) {
		glUniform3uiv(uniform, buffer);
	}

	public final void setUniform4ui(int uniform, Vector4i vector) {
		glUniform4ui(uniform, vector.x, vector.y, vector.z, vector.w);
	}

	public final void setUniform4ui(int uniform, int i1, int i2, int i3, int i4) {
		glUniform4ui(uniform, i1, i2, i3, i4);
	}

	public final void setUniform4ui(int uniform, int[] arr) {
		glUniform4uiv(uniform, arr);
	}

	public final void setUniform4ui(int uniform, IntBuffer buffer) {
		glUniform4uiv(uniform, buffer);
	}

	public final void setUniformf(int uniform, float f) {
		glUniform1f(uniform, f);
	}

	public final void setUniformf(int uniform, float[] arr) {
		glUniform1fv(uniform, arr);
	}

	public final void setUniformf(int uniform, FloatBuffer buffer) {
		glUniform1fv(uniform, buffer);
	}

	public final void setUniform2f(int uniform, Vector2f vector) {
		glUniform2f(uniform, vector.x, vector.y);
	}

	public final void setUniform2f(int uniform, float f1, float f2) {
		glUniform2f(uniform, f1, f2);
	}

	public final void setUniform2f(int uniform, float[] arr) {
		glUniform2fv(uniform, arr);
	}

	public final void setUniform2f(int uniform, FloatBuffer buffer) {
		glUniform2fv(uniform, buffer);
	}

	public final void setUniform3f(int uniform, Vector3f vector) {
		glUniform3f(uniform, vector.x, vector.y, vector.z);
	}

	public final void setUniform3f(int uniform, float f1, float f2, float f3) {
		glUniform3f(uniform, f1, f2, f3);
	}

	public final void setUniform3f(int uniform, float[] arr) {
		glUniform3fv(uniform, arr);
	}

	public final void setUniform3f(int uniform, FloatBuffer buffer) {
		glUniform3fv(uniform, buffer);
	}

	public final void setUniform4f(int uniform, Vector4f vector) {
		glUniform4f(uniform, vector.x, vector.y, vector.z, vector.w);
	}

	public final void setUniform4f(int uniform, Quaternionf quaternion) {
		glUniform4f(uniform, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public final void setUniform4f(int uniform, float f1, float f2, float f3, float f4) {
		glUniform4f(uniform, f1, f2, f3, f4);
	}

	public final void setUniform4f(int uniform, float[] arr) {
		glUniform4fv(uniform, arr);
	}

	public final void setUniform4f(int uniform, FloatBuffer buffer) {
		glUniform4fv(uniform, buffer);
	}

	public final void setUniformColor(int uniform, int color) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = Color.toFloatBuffer(stack.mallocFloat(4), color);
			buffer.flip();
			glUniform4fv(uniform, buffer);
		}
	}

	public final void setUniformMatrix3f(int uniform, float m00, float m01, float m02, float m10, float m11, float m12,
			float m20, float m21, float m22) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(9);
			buffer.put(m00);
			buffer.put(m01);
			buffer.put(m02);
			buffer.put(m10);
			buffer.put(m11);
			buffer.put(m12);
			buffer.put(m20);
			buffer.put(m21);
			buffer.put(m22);
			buffer.flip();
			glUniformMatrix3fv(uniform, true, buffer);
		}
	}

	public final void setUniformMatrix3f(int uniform, float[] matrix) {
		glUniformMatrix3fv(uniform, true, matrix);
	}

	public final void setUniformMatrix3f(int uniform, FloatBuffer buffer) {
		glUniformMatrix3fv(uniform, true, buffer);
	}

	public final void setUniformMatrix4f(int uniform, Matrix matrix) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = matrix.get(stack.mallocFloat(16));
			buffer.flip();
			glUniformMatrix4fv(uniform, true, buffer);
		}
	}

	public final void setUniformMatrix4f(int uniform, float m00, float m01, float m02, float m03, float m10, float m11,
			float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32,
			float m33) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(16);
			buffer.put(m00);
			buffer.put(m01);
			buffer.put(m02);
			buffer.put(m03);
			buffer.put(m10);
			buffer.put(m11);
			buffer.put(m12);
			buffer.put(m13);
			buffer.put(m20);
			buffer.put(m21);
			buffer.put(m22);
			buffer.put(m23);
			buffer.put(m30);
			buffer.put(m31);
			buffer.put(m32);
			buffer.put(m33);
			buffer.flip();
			glUniformMatrix4fv(uniform, true, buffer);
		}
	}

	public final void setUniformMatrix4f(int uniform, float[] matrix) {
		glUniformMatrix4fv(uniform, true, matrix);
	}

	public final void setUniformMatrix4f(int uniform, FloatBuffer buffer) {
		glUniformMatrix4fv(uniform, true, buffer);
	}

	protected final void setVertexShader(String text) {
		glDeleteShader(vertexShader);
		vertexShader = addShader(text, GL_VERTEX_SHADER);
	}

	public final int getVertexShader() {
		return vertexShader;
	}

	protected final void setGeometryShader(String text) {
		glDeleteShader(geometryShader);
		geometryShader = addShader(text, GL_GEOMETRY_SHADER);
	}

	public final int getGeometryShader() {
		return geometryShader;
	}

	protected final void setFragmentShader(String text) {
		glDeleteShader(fragmentShader);
		fragmentShader = addShader(text, GL_FRAGMENT_SHADER);
	}

	public final int getFragmentShader() {
		return fragmentShader;
	}

	private final int addShader(String text, int type) {
		int shader = glCreateShader(type);
		if (shader == 0) {
			Logger.err("Shader creation failed: adding shader");
			System.exit(1);
		}
		glShaderSource(shader, text);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
			Logger.err(glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}

		glAttachShader(program, shader);
		return shader;
	}

	protected final void compileShader() {
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
			Logger.err(glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}

		glValidateProgram(program);
		if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
			Logger.err(glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
	}
}
