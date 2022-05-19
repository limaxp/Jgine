package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.util.ArrayList;
import java.util.List;

import org.jgine.misc.math.vector.Vector2i;
import org.lwjgl.PointerBuffer;

public class DisplayManager {

	public static void init() {
		glfwSetMonitorCallback(DisplayManager::monitor_callback);
	}

	private static void monitor_callback(long monitor, int event) {
		if (event == GLFW_CONNECTED) {

		} else if (event == GLFW_DISCONNECTED) {

		}
	}

	public static Display getPrimaryDisplay() {
		return new Display(glfwGetPrimaryMonitor());
	}

	public static List<Display> getDisplays() {
		List<Display> result = new ArrayList<Display>();
		PointerBuffer buffer = glfwGetMonitors();
		while (buffer.hasRemaining()) {
			result.add(new Display(buffer.get()));
		}
		return result;
	}

	public static Display getDisplay(String name) {
		PointerBuffer buffer = glfwGetMonitors();
		while (buffer.hasRemaining()) {
			long id = buffer.get();
			if (glfwGetMonitorName(id).equals(name))
				return new Display(id);
		}
		return getPrimaryDisplay();
	}

	public static Display getDisplay(int index) {
		return new Display(glfwGetMonitors().get(index));
	}

	public static Display getDisplay(Vector2i pos) {
		return getDisplay(pos.x, pos.y);
	}

	public static Display getDisplay(int x, int y) {
		for (Display display : getDisplays()) {
			Vector2i pos = display.getVirtualPosition();
			Vector2i res = display.getResolution();
			if (x >= pos.x && x < pos.x + res.x && y >= pos.y && y < pos.y + res.y)
				return display;
		}
		return getPrimaryDisplay();
	}
}
