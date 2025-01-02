package org.jgine.render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_CLEAR_VALUE;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

import org.jgine.utils.Color;
import org.jgine.utils.options.Options;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Helper class for openGL.
 */
public class OpenGL {

	public static final int VERSION_MAJOR = 3;
	public static final int VERSION_MINOR = 3;
	public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;

	private static GLCapabilities capabilities;

	public static void init() {
		capabilities = GL.createCapabilities();
		if (!capabilities.GL_ARB_shader_objects)
			throw new AssertionError("ARB_fragment_shader extension required.");
		if (!capabilities.GL_ARB_vertex_shader)
			throw new AssertionError("ARB_fragment_shader extension required.");
		if (!capabilities.GL_ARB_fragment_shader)
			throw new AssertionError("ARB_fragment_shader extension required.");

		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glEnable(GL_MULTISAMPLE);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		if (Options.DEBUG)
			GLUtil.setupDebugMessageCallback();
	}

	public static void terminate() {
		GL.destroy();
	}

	public static void enableWireframeMode() {
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}

	public static void disableWireframeMode() {
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

	public static void enableDepthTest() {
		glEnable(GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		glDisable(GL_DEPTH_TEST);
	}

	/**
	 * Disable this to not write to depth buffer. Can be used to draw transparent
	 * objects!
	 */
	public static void enableDepthMask() {
		glDepthMask(true);
	}

	/**
	 * Disable this to not write to depth buffer. Can be used to draw transparent
	 * objects!
	 */
	public static void disableDepthMask() {
		glDepthMask(false);
	}

	public static void setClearColor(float red, float green, float blue, float alpha) {
		glClearColor(red, green, blue, alpha);
	}

	public static void setClearColor(int color) {
		glClearColor((float) Color.red(color) / 255, (float) Color.green(color) / 255, (float) Color.blue(color) / 255,
				(float) Color.alpha(color) / 255);
	}

	public static int getClearColor() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(4);
			glGetFloatv(GL_COLOR_CLEAR_VALUE, buffer);
			return Color.rgba(buffer.get(), buffer.get(), buffer.get(), buffer.get());
		}
	}

	public static void bindVertexArray(int vao) {
		glBindVertexArray(vao);
	}

	public static void drawArrays(int mode, int first, int count) {
		glDrawArrays(mode, first, count);
	}

	public static void drawElements(int mode, int count, int type, long indices) {
		glDrawElements(mode, count, type, indices);
	}

	public static void drawArraysInstanced(int mode, int first, int count, int primcount) {
		glDrawArraysInstanced(mode, first, count, primcount);
	}

	public static void drawElementsInstanced(int mode, int count, int type, long indices, int primcount) {
		glDrawElementsInstanced(mode, count, type, indices, primcount);
	}

	public static GLCapabilities getCapabilities() {
		return capabilities;
	}

	public static int getError() {
		return glGetError();
	}

	public static void checkError(Consumer<Integer> func) {
		int err;
		while ((err = glGetError()) != GL_NO_ERROR) {
			func.accept(err);
		}
	}

	public static void setDebugMessageCallback(GLDebugMessageCallbackI callback) {
		glDebugMessageCallback(callback, 0);
	}

	public static String pointerToString(long message, int length) {
		return MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length));
	}
}