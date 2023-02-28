package org.jgine.core.window;

import static org.lwjgl.glfw.GLFW.glfwGetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPhysicalSize;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorUserPointer;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorWorkarea;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwSetGamma;
import static org.lwjgl.glfw.GLFW.glfwSetGammaRamp;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorUserPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.math.vector.Vector4i;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

/**
 * A glfw monitor.
 * <p>
 * Documentation: <a href=
 * "https://www.glfw.org/docs/3.3/group__monitor.html">https://www.glfw.org/docs/3.3/group__monitor.html</a>
 */
public class Display {

	public final long id;
	private GLFWVidMode vidMode;

	Display(long id) {
		this.id = id;
		vidMode = glfwGetVideoMode(id);
	}

	public void update() {
		vidMode = glfwGetVideoMode(id);
	}

	public int getWidth() {
		return vidMode.width();
	}

	public int getHeight() {
		return vidMode.height();
	}

	public Vector2i getResolution() {
		return new Vector2i(vidMode.width(), vidMode.height());
	}

	public int getRefreshRate() {
		return vidMode.refreshRate();
	}

	public int getGreenBits() {
		return vidMode.greenBits();
	}

	public int getRedBits() {
		return vidMode.redBits();
	}

	public int getBlueBits() {
		return vidMode.blueBits();
	}

	public String getName() {
		return glfwGetMonitorName(id);
	}

	public Vector2i getPhysicalSize() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			glfwGetMonitorPhysicalSize(id, width, height);
			return new Vector2i(width.get(0), height.get(0));
		}
	}

	public Vector2f getContentScale() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer x = stack.mallocFloat(1);
			FloatBuffer y = stack.mallocFloat(1);
			glfwGetMonitorContentScale(id, x, y);
			return new Vector2f(x.get(0), y.get(0));
		}
	}

	public Vector2i getVirtualPosition() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			glfwGetMonitorPos(id, x, y);
			return new Vector2i(x.get(0), y.get(0));
		}
	}

	public Vector4i getWorkArea() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			glfwGetMonitorWorkarea(id, x, y, width, height);
			return new Vector4i(x.get(0), y.get(0), width.get(0), height.get(0));
		}
	}

	public void setUserPointer(long userPointer) {
		glfwSetMonitorUserPointer(id, userPointer);
	}

	public long getUserPointer() {
		return glfwGetMonitorUserPointer(id);
	}

	public void setGamma(float gamma) {
		glfwSetGamma(id, gamma);
	}

	public void setGammaRamp(GammaRamp gamma) {
		glfwSetGammaRamp(id, gamma.glfw);
	}

	public GammaRamp getGammaRamp() {
		return new GammaRamp(glfwGetGammaRamp(id));
	}

	@Override
	public String toString() {
		return super.toString() + "[" + getName() + ":" + getWidth() + "/" + getHeight() + "]";
	}
}
