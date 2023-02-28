package org.jgine.core.window;

import java.nio.ShortBuffer;

import org.lwjgl.glfw.GLFWGammaRamp;

/**
 * The gamma ramp for a {@link Display}.
 */
public class GammaRamp {

	protected final GLFWGammaRamp glfw;

	public GammaRamp(GLFWGammaRamp glfw) {
		this.glfw = glfw;
	}

	public void size(int size) {
		glfw.size(size);
	}

	public int size() {
		return glfw.size();
	}

	public void red(ShortBuffer value) {
		glfw.red(value);
	}

	public ShortBuffer red() {
		return glfw.red();
	}

	public void green(ShortBuffer value) {
		glfw.green(value);
	}

	public ShortBuffer green() {
		return glfw.green();
	}

	public void blue(ShortBuffer value) {
		glfw.blue(value);
	}

	public ShortBuffer blue() {
		return glfw.blue();
	}
}
