package org.jgine.core;

import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwDestroyCursor;

import org.jgine.misc.utils.logger.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;

public class GLFWHelper {

	public static void init() {
		GLFWErrorCallback.createPrint(Logger.getErrorPrintStream()).set();
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		Logger.log(() -> "Engine: GLFW - Version: " + GLFW.glfwGetVersionString());
	}

	public static void terminate() {
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public static void pollGLFWEvents() {
		GLFW.glfwPollEvents();
	}

	public static long createCursor(GLFWImage image, int xhot, int yhot) {
		return glfwCreateCursor(image, xhot, yhot);
	}

	public static long createStandardCursor(int shape) {
		return glfwCreateStandardCursor(shape);
	}

	public static void deleteCursor(long cursor) {
		glfwDestroyCursor(cursor);
	}
}
