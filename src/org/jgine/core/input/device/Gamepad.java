package org.jgine.core.input.device;

import static org.lwjgl.glfw.GLFW.glfwGetGamepadName;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;

import org.jgine.core.input.Key;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWGamepadState;

public class Gamepad extends Joystick {

	private GLFWGamepadState gamepadState;

	public Gamepad(int slot) {
		super(slot);
		gamepadState = new GLFWGamepadState(BufferUtils.createByteBuffer(GLFWGamepadState.SIZEOF));
	}

	@Override
	public void update() {
		glfwGetGamepadState(slot, gamepadState);
	}

	@Override
	public boolean isKeyPressed(int key) {
		return gamepadState.buttons(key) == 1;
	}

	@Override
	public boolean isKeyReleased(int key) {
		return gamepadState.buttons(key) == 0;
	}

	@Override
	public String getName() {
		return glfwGetGamepadName(slot);
	}

	@Override
	public boolean isGamepad() {
		return true;
	}

	public float getAxisLeftX() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_LEFT_X);
	}

	public float getAxisLeftY() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_LEFT_Y);
	}

	public float getAxisRightX() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_RIGHT_X);
	}

	public float getAxisRightY() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_RIGHT_Y);
	}
}
