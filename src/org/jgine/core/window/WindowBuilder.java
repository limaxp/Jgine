package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY;
import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_CREATION_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_NO_ERROR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_REVISION;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_ROBUSTNESS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUS_ON_SHOW;
import static org.lwjgl.glfw.GLFW.GLFW_HOVERED;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRANSPARENT_FRAMEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowHintString;

import org.jgine.render.OpenGL;

public class WindowBuilder {

	public static class Attribute {

		public static final int FOCUSED = GLFW_FOCUSED;
		public static final int ICONIFIED = GLFW_ICONIFIED;
		public static final int MAXIMIZED = GLFW_MAXIMIZED;
		public static final int HOVERED = GLFW_HOVERED;
		public static final int VISIBLE = GLFW_VISIBLE;
		public static final int RESIZABLE = GLFW_RESIZABLE;
		public static final int DECORATED = GLFW_DECORATED;
		public static final int AUTO_ICONIFY = GLFW_AUTO_ICONIFY;
		public static final int FLOATING = GLFW_FLOATING;
		public static final int TRANSPARENT_FRAMEBUFFER = GLFW_TRANSPARENT_FRAMEBUFFER;
		public static final int FOCUS_ON_SHOW = GLFW_FOCUS_ON_SHOW;
		public static final int CLIENT_API = GLFW_CLIENT_API;
		public static final int CONTEXT_CREATION_API = GLFW_CONTEXT_CREATION_API;
		public static final int CONTEXT_VERSION_MAJOR = GLFW_CONTEXT_VERSION_MAJOR;
		public static final int CONTEXT_VERSION_MINOR = GLFW_CONTEXT_VERSION_MINOR;
		public static final int CONTEXT_REVISION = GLFW_CONTEXT_REVISION;
		public static final int OPENGL_FORWARD_COMPAT = GLFW_OPENGL_FORWARD_COMPAT;
		public static final int OPENGL_DEBUG_CONTEXT = GLFW_OPENGL_DEBUG_CONTEXT;
		public static final int OPENGL_PROFILE = GLFW_OPENGL_PROFILE;
		public static final int CONTEXT_RELEASE_BEHAVIOR = GLFW_CONTEXT_RELEASE_BEHAVIOR;
		public static final int CONTEXT_NO_ERROR = GLFW_CONTEXT_NO_ERROR;
		public static final int CONTEXT_ROBUSTNESS = GLFW_CONTEXT_ROBUSTNESS;
	}

	public static void init() {
		glfwWindowHint(Attribute.CONTEXT_VERSION_MAJOR, OpenGL.VERSION_MAJOR);
		glfwWindowHint(Attribute.CONTEXT_VERSION_MINOR, OpenGL.VERSION_MINOR);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(Attribute.OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		setResizeAble(true);
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
