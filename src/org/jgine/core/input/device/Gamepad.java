package org.jgine.core.input.device;

import static org.lwjgl.glfw.GLFW.glfwGetGamepadName;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;

import org.jgine.core.input.Key;
import org.jgine.misc.math.vector.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWGamepadState;

public class Gamepad extends Joystick {

	private GLFWGamepadState gamepadState;

	public Gamepad(int slot) {
		super(Key.GAMEPAD_BUTTON_LAST + 1, slot);
		gamepadState = new GLFWGamepadState(BufferUtils.createByteBuffer(GLFWGamepadState.SIZEOF));
	}

	@Override
	public void poll() {
		glfwGetGamepadState(slot, gamepadState);
		check(Key.GAMEPAD_BUTTON_CROSS);
		check(Key.GAMEPAD_BUTTON_CIRCLE);
		check(Key.GAMEPAD_BUTTON_SQUARE);
		check(Key.GAMEPAD_BUTTON_TRIANGLE);
		check(Key.GAMEPAD_BUTTON_LEFT_BUMPER);
		check(Key.GAMEPAD_BUTTON_RIGHT_BUMPER);
		check(Key.GAMEPAD_BUTTON_BACK);
		check(Key.GAMEPAD_BUTTON_START);
		check(Key.GAMEPAD_BUTTON_GUIDE);
		check(Key.GAMEPAD_BUTTON_LEFT_THUMB);
		check(Key.GAMEPAD_BUTTON_RIGHT_THUMB);
		check(Key.GAMEPAD_BUTTON_DPAD_UP);
		check(Key.GAMEPAD_BUTTON_DPAD_RIGHT);
		check(Key.GAMEPAD_BUTTON_DPAD_DOWN);
		check(Key.GAMEPAD_BUTTON_DPAD_LEFT);
	}

	private void check(int key) {
		boolean isPressed = isPressedIntern(key);
		if (gamepadState.buttons(key) == 1)
			if (isPressed)
				repeat(key);
			else
				press(key);
		else if (isPressed)
			release(key);
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

	public Vector2f getAxisLeft() {
		return new Vector2f(getAxisLeftX(), getAxisLeftY());
	}

	public float getAxisLeftX() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_LEFT_X);
	}

	public float getAxisLeftY() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_LEFT_Y);
	}

	public Vector2f getAxisRight() {
		return new Vector2f(getAxisRightX(), getAxisRightY());
	}

	public float getAxisRightX() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_RIGHT_X);
	}

	public float getAxisRightY() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_RIGHT_Y);
	}

	public float getTriggerLeft() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_LEFT_TRIGGER);
	}

	public float getTriggerRight() {
		return gamepadState.axes(Key.GAMEPAD_AXIS_RIGHT_TRIGGER);
	}
}
