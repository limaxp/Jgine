package org.jgine.render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import org.jgine.misc.utils.options.Options;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;

/**
 * @author Maximilian Paar
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

		// glfwSwapInterval(1); // Enable v-sync
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glEnable(GL_MULTISAMPLE);  
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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

	public static GLCapabilities getCapabilities() {
		return capabilities;
	}

	public static int getError() {
		return glGetError();
	}
}
