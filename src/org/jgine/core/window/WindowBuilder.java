package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowHintString;

import org.jgine.core.window.Window.Attribute;
import org.jgine.render.OpenGL;
import org.jgine.utils.Options;

/**
 * A helper class for building {@link Window}<code>s</code>. New instances of
 * {@link Window} will use the setting specified here.
 * <p>
 * See Also: <a href=
 * "https://www.glfw.org/docs/3.3/window_guide.html">https://www.glfw.org/docs/3.3/window_guide.html</a>
 */
public class WindowBuilder {

	public static void init() {
		glfwWindowHint(Attribute.CONTEXT_VERSION_MAJOR, OpenGL.VERSION_MAJOR);
		glfwWindowHint(Attribute.CONTEXT_VERSION_MINOR, OpenGL.VERSION_MINOR);
		glfwWindowHint(Attribute.OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(Attribute.OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		setResizeAble(true);
		setMultiSamples(Options.ANTI_ALIASING.getInt());
	}

	public static void setFocused(boolean bool) {
		setAttribute(Attribute.FOCUSED, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setIconified(boolean bool) {
		setAttribute(Attribute.ICONIFIED, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setMaximized(boolean bool) {
		setAttribute(Attribute.MAXIMIZED, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setHovered(boolean bool) {
		setAttribute(Attribute.HOVERED, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setVisible(boolean bool) {
		setAttribute(Attribute.VISIBLE, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setResizeAble(boolean bool) {
		setAttribute(Attribute.RESIZABLE, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setDecorated(boolean bool) {
		setAttribute(Attribute.DECORATED, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setAutoIconify(boolean bool) {
		setAttribute(Attribute.AUTO_ICONIFY, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setFloating(boolean bool) {
		setAttribute(Attribute.FLOATING, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setTransparentFramebuffer(boolean bool) {
		setAttribute(Attribute.TRANSPARENT_FRAMEBUFFER, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setFocusOnShow(boolean bool) {
		setAttribute(Attribute.FOCUS_ON_SHOW, bool ? GLFW_TRUE : GLFW_FALSE);
	}

	public static void setMultiSamples(int amount) {
		setAttribute(Attribute.SAMPLES, amount);
	}

	public static void setAttribute(int attribute, int value) {
		glfwWindowHint(attribute, value);
	}

	public static void setAttribute(int attribute, String value) {
		glfwWindowHintString(attribute, value);
	}

	public static void reset() {
		glfwDefaultWindowHints();
		init();
	}
}
