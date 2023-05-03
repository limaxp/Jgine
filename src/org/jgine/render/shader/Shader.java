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
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
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
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL30.glUniform1ui;
import static org.lwjgl.opengl.GL30.glUniform1uiv;
import static org.lwjgl.opengl.GL30.glUniform2ui;
import static org.lwjgl.opengl.GL30.glUniform2uiv;
import static org.lwjgl.opengl.GL30.glUniform3ui;
import static org.lwjgl.opengl.GL30.glUniform3uiv;
import static org.lwjgl.opengl.GL30.glUniform4ui;
import static org.lwjgl.opengl.GL30.glUniform4uiv;
import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Material;
import org.jgine.utils.Color;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.rotation.Quaternionf;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector2i;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector3i;
import org.jgine.utils.math.vector.Vector4f;
import org.jgine.utils.math.vector.Vector4i;
import org.lwjgl.system.MemoryStack;

public class Shader {

	public static final int VERTEX_SHADER = GL_VERTEX_SHADER;
	public static final int GEOMETRY_SHADER = GL_GEOMETRY_SHADER;
	public static final int FRAGMENT_SHADER = GL_FRAGMENT_SHADER;

	public static final int INTERLEAVED_ATTRIBS = GL_INTERLEAVED_ATTRIBS;
	public static final int SEPARATE_ATTRIBS = GL_SEPARATE_ATTRIBS;

	public static final Shader NULL = new Shader(0) {

		@Override
		public void setTransform(Matrix matrix, Matrix projectionMatrix) {
		}

		@Override
		public void setMaterial(Material material) {
		}
	};

	protected final int program;

	public Shader(int program) {
		this.program = program;
	}

	public Shader() {
		program = glCreateProgram();
		if (program == 0) {
			Logger.err("Shader: Creation failed!");
			return;
		}
	}

	public Shader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		this();
		compile(VERTEX_SHADER, vertex);
		compile(GEOMETRY_SHADER, geometry);
		compile(FRAGMENT_SHADER, fragment);
		link();
	}

	public final void delete() {
		glDeleteProgram(program);
	}

	public final int compile(int type, @Nullable String text) {
		if (text == null)
			return 0;

		int shader = glCreateShader(type);
		if (shader == 0) {
			Logger.err("Shader creation failed: adding shader");
			return 0;
		}
		glShaderSource(shader, text);
		glCompileShader(shader);
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
			Logger.err(glGetShaderInfoLog(shader, 1024));
			return 0;
		}
		glAttachShader(program, shader);
		glDeleteShader(shader);
		return shader;
	}

	public final void link() {
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
			Logger.err(glGetProgramInfoLog(program, 1024));
			return;
		}
		glValidateProgram(program);
		if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
			Logger.err(glGetProgramInfoLog(program, 1024));
			return;
		}
	}

	public void bind() {
		glUseProgram(program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void setTransform(Matrix matrix, Matrix projectionMatrix) {
	}

	public void setMaterial(Material material) {
	}

	public final void setTransformFeedback(int mode, String value) {
		glTransformFeedbackVaryings(program, value, mode);
	}

	public final void setTransformFeedback(int mode, String... values) {
		glTransformFeedbackVaryings(program, values, mode);
	}

	public final int addUniform(String uniform) {
		int uniformLoc = glGetUniformLocation(program, uniform);
		if (uniformLoc == -1)
			Logger.err("Shader: Uniform '" + uniform + "' adding failed!");
		return uniformLoc;
	}

	public final int[] addUniforms(String uniform, int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++)
			result[i] = addUniform(uniform + '[' + i + ']');
		return result;
	}

	public final int[][] addUniforms(String uniform, int size, String[] values) {
		int[][] result = new int[size][];
		for (int i = 0; i < size; i++) {
			int[] sub = new int[values.length];
			result[i] = sub;
			String name = uniform + '[' + i + "].";
			for (int j = 0; j < values.length; j++)
				sub[j] = addUniform(name + values[j]);
		}
		return result;
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
}
