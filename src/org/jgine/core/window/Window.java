package org.jgine.core.window;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY;
import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_CREATION_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_NO_ERROR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_REVISION;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_ROBUSTNESS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUS_ON_SHOW;
import static org.lwjgl.glfw.GLFW.GLFW_HOVERED;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.GLFW_LOCK_KEY_MODS;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RAW_MOUSE_MOTION;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_MOUSE_BUTTONS;
import static org.lwjgl.glfw.GLFW.GLFW_TRANSPARENT_FRAMEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwRawMouseMotionSupported;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.input.Input;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.OpenGL;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.system.MemoryStack;

/**
 * Represents the base class of a window.
 * 
 * @author Maximilian Paar
 */
public class Window {

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

	private long id;
	private String title;
	private int width;
	private int height;
	private boolean isFullScreen;
	private boolean isFocused;

	public Window(String title, int width, int height, boolean isFullScreen) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.isFullScreen = isFullScreen;
		this.isFocused = true;
		create();
	}

	protected void create() {
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, OpenGL.VERSION_MAJOR);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, OpenGL.VERSION_MINOR);
		// glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		Display display = DisplayManager.getDisplay(Options.MONITOR.getInt());
		if (isFullScreen) {
			id = glfwCreateWindow(width, height, title, display.getId(), 0);
		} else {
			id = glfwCreateWindow(width, height, title, 0, 0);
			center(display);
		}
		if (id == 0)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwMakeContextCurrent(id); // Make the OpenGL context current
		show();
		glfwSetWindowSizeCallback(id, this::resizeCallback);
		glfwSetWindowFocusCallback(id, this::focusCallback);
	}

	public void delete() {
		glfwFreeCallbacks(id);
		glfwDestroyWindow(id);
	}

	public void swapBuffers() {
		glfwSwapBuffers(id);
	}

	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(id, shouldClose);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(id);
	}

	public void setTitle(@NonNull String title) {
		this.title = title;
		glfwSetWindowTitle(id, title);
	}

	public void setPosition(Vector2i vec) {
		glfwSetWindowPos(id, vec.x, vec.y);
	}

	public void setPosition(int x, int y) {
		glfwSetWindowPos(id, x, y);
	}

	public Vector2i getPosition() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			glfwGetWindowPos(id, x, y);
			return new Vector2i(x.get(0), y.get(0));
		}
	}

	public void minimize() {
		glfwIconifyWindow(id);
	}

	public void restore() {
		glfwRestoreWindow(id);
	}

	public void hide() {
		glfwHideWindow(id);
	}

	public void show() {
		glfwShowWindow(id);
	}

	@Nullable
	public Display getDisplay() {
		long monitor = glfwGetWindowMonitor(id);
		if (monitor <= 0)
			return null;
		return new Display(monitor);
	}

	public void toggleFullScreen() {
		if (isFullScreen)
			setWindowed();
		else
			setFullScreen();
	}

	public void setFullScreen() {
		glfwSetWindowMonitor(id, DisplayManager.getDisplay(getPosition()).getId(), 0, 0, width, height, 0);
		isFullScreen = true;
	}

	public void setWindowed() {
		Display display = getDisplay();
		if (display == null)
			return;
		Vector2i displayPos = display.getVirtualPosition();
		glfwSetWindowMonitor(id, 0, displayPos.x, displayPos.y, width, height, 0);
		isFullScreen = false;
		center();
	}

	public void center() {
		center(DisplayManager.getDisplay(getPosition()));
	}

	public void center(Display display) {
		Vector2i displayPos = display.getVirtualPosition();
		setPosition(displayPos.x + (display.getWidth() - width) / 2, displayPos.y + (display.getHeight() - height) / 2);
	}

	public void hideCursor() {
		setInputMode(GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}

	public void showCursor() {
		setInputMode(GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	public void disableCursor() {
		setInputMode(GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	public void enableStickyKeys() {
		setInputMode(GLFW_STICKY_KEYS, GLFW_TRUE);
	}

	public void disableStickyKeys() {
		setInputMode(GLFW_STICKY_KEYS, GLFW_FALSE);
	}

	public void toggleStickyKeys() {
		if (getInputMode(GLFW_STICKY_KEYS) == GLFW_TRUE)
			disableStickyKeys();
		else
			enableStickyKeys();
	}

	public void enableStickyMouseButtons() {
		setInputMode(GLFW_STICKY_MOUSE_BUTTONS, GLFW_TRUE);
	}

	public void disableStickyMouseButtons() {
		setInputMode(GLFW_STICKY_MOUSE_BUTTONS, GLFW_FALSE);
	}

	public void toggleStickyMouseButtons() {
		if (getInputMode(GLFW_STICKY_MOUSE_BUTTONS) == GLFW_TRUE)
			enableStickyMouseButtons();
		else
			disableStickyMouseButtons();
	}

	public void enableLockKeyMods() {
		setInputMode(GLFW_LOCK_KEY_MODS, GLFW_TRUE);
	}

	public void disableLockKeyMods() {
		setInputMode(GLFW_LOCK_KEY_MODS, GLFW_FALSE);
	}

	public void toggleLockKeyMods() {
		if (getInputMode(GLFW_LOCK_KEY_MODS) == GLFW_TRUE)
			disableLockKeyMods();
		else
			enableLockKeyMods();
	}

	public void enableRawMouseMotion() {
		if (glfwRawMouseMotionSupported())
			setInputMode(GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
	}

	public void disableRawMouseMotion() {
		setInputMode(GLFW_RAW_MOUSE_MOTION, GLFW_FALSE);
	}

	public void toggleRawMouseMotion() {
		if (getInputMode(GLFW_RAW_MOUSE_MOTION) == GLFW_TRUE)
			enableRawMouseMotion();
		else
			disableRawMouseMotion();
	}

	public void setInputMode(int mode, int value) {
		glfwSetInputMode(id, mode, value);
	}

	public int getInputMode(int mode) {
		return glfwGetInputMode(id, mode);
	}

	public int getKey(int key) {
		return glfwGetKey(id, key);
	}

	public int getMouseButton(int key) {
		return glfwGetMouseButton(id, key);
	}

	public void setCursorPos(double x, double y) {
		glfwSetCursorPos(id, x, y);
	}

	public void setCursorPos(Vector2f vec) {
		glfwSetCursorPos(id, vec.x, vec.y);
	}

	public Vector2f getCursorPos() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);
			glfwGetCursorPos(id, xPos, yPos);
			return new Vector2f((float) xPos.get(0), (float) yPos.get(0));
		}
	}

	public void setCursor(long cursor) {
		glfwSetCursor(id, cursor);
	}

	public void setDefaultCursor() {
		glfwSetCursor(id, 0);
	}

	public void setAttribute(int attribute, int value) {
		glfwSetWindowAttrib(id, attribute, value);
	}

	public int getAttribute(int attribute) {
		return glfwGetWindowAttrib(id, attribute);
	}

	public long getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isFullScreen() {
		return isFullScreen;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setIcon(GLFWImage.Buffer images) {
		glfwSetWindowIcon(id, images);
	}

	public void setKeyCallback(KeyCallback callback) {
		glfwSetKeyCallback(id, callback);
	}

	public void setCharCallback(CharCallback callback) {
		glfwSetCharCallback(id, callback);
	}

	public void setCursorPosCallback(CursorPosCallback callback) {
		glfwSetCursorPosCallback(id, callback);
	}

	public void setMouseButtonCallback(MouseButtonCallback callback) {
		glfwSetMouseButtonCallback(id, callback);
	}

	public void setCursorEnterCallback(CursorEnterCallback callback) {
		glfwSetCursorEnterCallback(id, callback);
	}

	public void setScrollCallback(ScrollCallback callback) {
		glfwSetScrollCallback(id, callback);
	}

	protected void resizeCallback(long window, int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected void focusCallback(long window, boolean isFocused) {
		this.isFocused = isFocused;
		if (isFocused)
			Input.setWindow(this);
	}

	/**
	 * scanCode is platform dependent but safe to save to disk!
	 * 
	 * @author Max
	 */
	@FunctionalInterface
	public static interface KeyCallback extends GLFWKeyCallbackI {
	}

	/**
	 * scanCode is platform dependent but safe to save to disk!
	 * 
	 * @author Max
	 */
	@FunctionalInterface
	public static interface CharCallback extends GLFWCharCallbackI {
	}

	@FunctionalInterface
	public static interface CursorPosCallback extends GLFWCursorPosCallbackI {
	}

	@FunctionalInterface
	public static interface MouseButtonCallback extends GLFWMouseButtonCallbackI {
	}

	@FunctionalInterface
	public static interface CursorEnterCallback extends GLFWCursorEnterCallbackI {
	}

	@FunctionalInterface
	public static interface ScrollCallback extends GLFWScrollCallbackI {
	}
}
