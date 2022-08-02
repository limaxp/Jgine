package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.vector.Vector2i;
import org.lwjgl.PointerBuffer;

public class DisplayManager {

	private static final List<Display> DISPLAYS = new ArrayList<Display>();
	private static final List<Display> VIEW_LIST = Collections.unmodifiableList(DISPLAYS);

	public static void init() {
		PointerBuffer buffer = glfwGetMonitors();
		while (buffer.hasRemaining()) {
			DISPLAYS.add(new Display(buffer.get()));
		}
		glfwSetMonitorCallback(DisplayManager::monitor_callback);
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

	@Nullable
	public static Display getDisplay(Vector2i pos) {
		return getDisplay(pos.x, pos.y);
	}

	@Nullable
	public static Display getDisplay(int x, int y) {
		for (Display display : getDisplays()) {
			Vector2i pos = display.getVirtualPosition();
			Vector2i res = display.getResolution();
			if (x >= pos.x && x < pos.x + res.x && y >= pos.y && y < pos.y + res.y)
				return display;
		}
		return null;
	}
}
