package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.input.Cursor;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.utils.logger.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * Manager for handling {@link Display}<code>s</code>. Can be used to get
 * currently plugged in {@link Display}<code>s</code>.
 */
public class DisplayManager {

	private static final List<Display> DISPLAYS = new ArrayList<Display>();
	private static final List<Display> VIEW_LIST = Collections.unmodifiableList(DISPLAYS);

	public static void init() {
		initGLFW();
		PointerBuffer buffer = glfwGetMonitors();
		while (buffer.hasRemaining()) {
			DISPLAYS.add(new Display(buffer.get()));
		}
		glfwSetMonitorCallback(DisplayManager::monitor_callback);
		WindowBuilder.init();
	}

	public static void terminate() {
		terminateGLFW();
		Cursor.terminate();
	}

	private static void initGLFW() {
		GLFWErrorCallback.createPrint(Logger.getErrorPrintStream()).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		Logger.log(() -> "Engine: GLFW - Version: " + glfwGetVersionString());
	}

	private static void terminateGLFW() {
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void pollGLFWEvents() {
		glfwPollEvents();
	}

	private static void monitor_callback(long monitor, int event) {
		if (event == GLFW_CONNECTED) {
			DISPLAYS.add(new Display(monitor));
		} else if (event == GLFW_DISCONNECTED) {
			for (int i = DISPLAYS.size() - 1; i >= 0; i--) {
				if (DISPLAYS.get(i).id == monitor) {
					DISPLAYS.remove(i);
					break;
				}
			}
		}
	}

	public static Display getPrimaryDisplay() {
		return getDisplay(glfwGetPrimaryMonitor());
	}

	public static List<Display> getDisplays() {
		return VIEW_LIST;
	}

	@Nullable
	public static Display getDisplay(String name) {
		for (Display display : DISPLAYS)
			if (display.getName().equals(name))
				return display;
		return null;
	}

	@Nullable
	public static Display getDisplay(long id) {
		for (Display display : DISPLAYS)
			if (display.id == id)
				return display;
		return null;
	}

	public static Display getDisplay(int index) {
		return DISPLAYS.get(index);
	}

	public static Display getDisplay(Vector2i pos) {
		return getDisplay(pos.x, pos.y);
	}

	public static Display getDisplay(int x, int y) {
		Display nearestDisplay = null;
		float smallestDistance = Float.MAX_VALUE;
		for (Display display : getDisplays()) {
			Vector2i pos = display.getVirtualPosition();
			float distance = Vector2f.distance(x, y, pos.x, pos.y);
			if (distance < smallestDistance) {
				nearestDisplay = display;
				smallestDistance = distance;
			}
		}
		return nearestDisplay;
	}
}
