package org.jgine.core.window;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
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
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_HOVERED;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.GLFW_LOCK_KEY_MODS;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RAW_MOUSE_MOTION;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_REFRESH_RATE;
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
import static org.lwjgl.glfw.GLFW.glfwGetWindowContentScale;
import static org.lwjgl.glfw.GLFW.glfwGetWindowFrameSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowUserPointer;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowContentScaleCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSetWindowUserPointer;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.NonNull;
import org.jgine.core.input.Cursor;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.math.vector.Vector4i;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.OpenGL;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowContentScaleCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.glfw.GLFWWindowMaximizeCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
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

	public static class Mode {

		public static final int WINDOWED = 0;
		public static final int FULLSCREEN = 1;
		public static final int BORDERLESS = 2;
	}

	public final long id;
	private String title;
	private int resolutionX;
	private int resolutionY;
	private int mode;

	public Window(String title) {
		this(title, Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt(), Options.WINDOW_MODE.getInt());
	}

	public Window(String title, int resolutionX, int resolutionY, int mode) {
		this.title = title;
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		this.mode = mode;

		glfwWindowHint(Attribute.CONTEXT_VERSION_MAJOR, OpenGL.VERSION_MAJOR);
		glfwWindowHint(Attribute.CONTEXT_VERSION_MINOR, OpenGL.VERSION_MINOR);
		// glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(Attribute.OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(Attribute.VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(Attribute.RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(Attribute.FLOATING, GLFW_TRUE); // the window will be floating above all other

		Display display = DisplayManager.getDisplay(Options.MONITOR.getInt());
		glfwWindowHint(GLFW_RED_BITS, display.getRedBits());
		glfwWindowHint(GLFW_GREEN_BITS, display.getGreenBits());
		glfwWindowHint(GLFW_BLUE_BITS, display.getBlueBits());
		glfwWindowHint(GLFW_REFRESH_RATE, display.getRefreshRate());

		if (mode == Mode.BORDERLESS)
			glfwWindowHint(Attribute.DECORATED, GLFW_FALSE); // borderless

		if (mode == Mode.FULLSCREEN) {
			id = glfwCreateWindow(resolutionX, resolutionY, title, display.id, 0);
		} else {
			id = glfwCreateWindow(resolutionX, resolutionY, title, 0, 0);
			setWindowed(display);
		}
		if (id == 0)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwMakeContextCurrent(id); // Make the OpenGL context current
		show();
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

	public String getTitle() {
		return title;
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

	public Display getDisplay() {
		long monitor = glfwGetWindowMonitor(id);
		if (monitor <= 0)
			return DisplayManager.getDisplay(getPosition());
		return DisplayManager.getDisplay(monitor);
	}

	public void toggleMode() {
		if (isFullScreen())
			setBorderless();
		else if (isWindowed())
			setBorderless();
		else
			setFullScreen();
	}

	public void toggleFullScreen() {
		if (isFullScreen())
			setWindowed();
		else
			setFullScreen();
	}

	public void toggleBorderless() {
		if (isBorderless())
			setWindowed();
		else
			setBorderless();
	}

	public void setFullScreen() {
		if (!isFullScreen()) {
			mode = Mode.FULLSCREEN;
			Display display = getDisplay();
			glfwSetWindowMonitor(id, display.id, 0, 0, resolutionX, resolutionY, 0);
			display.update();
		}
	}

	public boolean isFullScreen() {
		return mode == Mode.FULLSCREEN;
	}

	public void setWindowed() {
		setAttribute(Attribute.DECORATED, 1);
		if (isFullScreen())
			setWindowed(getDisplay());
		else
			center();
		mode = Mode.WINDOWED;
	}

	private void setWindowed(Display display) {
		glfwSetWindowMonitor(id, 0, 0, 0, resolutionX, resolutionY, 0);
		display.update();
		center(display);
	}

	public boolean isWindowed() {
		return mode == Mode.WINDOWED;
	}

	public void setBorderless() {
		setAttribute(Attribute.DECORATED, 0);
		if (isFullScreen())
			setWindowed(getDisplay());
		else {
			setSize(resolutionX, resolutionY);
			center();
		}
		mode = Mode.BORDERLESS;
	}

	public boolean isBorderless() {
		return mode == Mode.BORDERLESS;
	}

	public void center() {
		center(getDisplay());
	}

	public void center(Display display) {
		Vector2i displayPos = display.getVirtualPosition();
		Vector4i framePosistions = getFrameSize();
		setPosition(displayPos.x + (display.getWidth() - resolutionX) / 2,
				displayPos.y + framePosistions.y + (display.getHeight() - resolutionY) / 2);
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

	public void setCursor(Cursor cursor) {
		glfwSetCursor(id, cursor.id);
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

	public void setSize(int x, int y) {
		glfwSetWindowSize(id, x, y);
	}

	public void setSize(Vector2i vec) {
		glfwSetWindowSize(id, vec.x, vec.y);
	}

	public Vector2i getSize() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer xSize = stack.mallocInt(1);
			IntBuffer ySize = stack.mallocInt(1);
			glfwGetWindowSize(id, xSize, ySize);
			return new Vector2i(xSize.get(0), ySize.get(0));
		}
	}

	public int getResolutionX() {
		return resolutionX;
	}

	public int getResolutionY() {
		return resolutionY;
	}

	public void setIcon(GLFWImage.Buffer images) {
		glfwSetWindowIcon(id, images);
	}

	public Vector4i getFrameSize() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer left = stack.mallocInt(1);
			IntBuffer top = stack.mallocInt(1);
			IntBuffer right = stack.mallocInt(1);
			IntBuffer bottom = stack.mallocInt(1);
			glfwGetWindowFrameSize(id, left, top, right, bottom);
			return new Vector4i(left.get(0), top.get(0), right.get(0), bottom.get(0));
		}
	}

	public Vector2f getContentScale() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer xScale = stack.mallocFloat(1);
			FloatBuffer yScale = stack.mallocFloat(1);
			glfwGetWindowContentScale(id, xScale, yScale);
			return new Vector2f(xScale.get(0), yScale.get(0));
		}
	}

	public void getOpacity(float opacity) {
		glfwSetWindowOpacity(id, opacity);
	}

	public float getOpacity() {
		return glfwGetWindowOpacity(id);
	}

	public void setUserPointer(long userPointer) {
		glfwSetWindowUserPointer(id, userPointer);
	}

	public long getUserPointer() {
		return glfwGetWindowUserPointer(id);
	}

	/**
	 * scanCode is platform dependent but safe to save to disk!
	 * 
	 * @author Max
	 */
	public void setKeyCallback(GLFWKeyCallbackI callback) {
		glfwSetKeyCallback(id, callback);
	}

	/**
	 * scanCode is platform dependent but safe to save to disk!
	 * 
	 * @author Max
	 */
	public void setCharCallback(GLFWCharCallbackI callback) {
		glfwSetCharCallback(id, callback);
	}

	public void setCursorPosCallback(GLFWCursorPosCallbackI callback) {
		glfwSetCursorPosCallback(id, callback);
	}

	public void setMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
		glfwSetMouseButtonCallback(id, callback);
	}

	public void setCursorEnterCallback(GLFWCursorEnterCallbackI callback) {
		glfwSetCursorEnterCallback(id, callback);
	}

	public void setScrollCallback(GLFWScrollCallbackI callback) {
		glfwSetScrollCallback(id, callback);
	}

	public void setWindowSizeCallback(GLFWWindowSizeCallbackI callback) {
		glfwSetWindowSizeCallback(id, callback);
	}

	public void setWindowFocusCallback(GLFWWindowFocusCallbackI callback) {
		glfwSetWindowFocusCallback(id, callback);
	}

	public void setWindowCloseCallback(GLFWWindowCloseCallbackI callback) {
		glfwSetWindowCloseCallback(id, callback);
	}

	public void setWindowContentScaleCallback(GLFWWindowContentScaleCallbackI callback) {
		glfwSetWindowContentScaleCallback(id, callback);
	}

	public void setWindowIconifyCallback(GLFWWindowIconifyCallbackI callback) {
		glfwSetWindowIconifyCallback(id, callback);
	}

	public void setWindowMaximizeCallback(GLFWWindowMaximizeCallbackI callback) {
		glfwSetWindowMaximizeCallback(id, callback);
	}

	public void setWindowPosCallback(GLFWWindowPosCallbackI callback) {
		glfwSetWindowPosCallback(id, callback);
	}

	public void setWindowRefreshCallback(GLFWWindowRefreshCallbackI callback) {
		glfwSetWindowRefreshCallback(id, callback);
	}
}
