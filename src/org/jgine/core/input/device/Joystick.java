package org.jgine.core.input.device;

import static org.lwjgl.glfw.GLFW.GLFW_HAT_CENTERED;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_LEFT_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_LEFT_UP;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_RIGHT_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_RIGHT_UP;
import static org.lwjgl.glfw.GLFW.GLFW_HAT_UP;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_10;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_11;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_12;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_13;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_14;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_15;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_16;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_3;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_4;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_6;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_7;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_8;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_9;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_LAST;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickHats;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickUserPointer;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickUserPointer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;

/**
 * a joystick {@link InputDevice}. Has methods for joystick buttons, hats and
 * axes.
 */
public class Joystick extends InputDevice {

	public static class Hats {

		public static final int HAT_CENTERED = GLFW_HAT_CENTERED;
		public static final int HAT_UP = GLFW_HAT_UP;
		public static final int HAT_RIGHT = GLFW_HAT_RIGHT;
		public static final int HAT_DOWN = GLFW_HAT_DOWN;
		public static final int HAT_LEFT = GLFW_HAT_LEFT;
		public static final int HAT_RIGHT_UP = GLFW_HAT_RIGHT_UP;
		public static final int HAT_RIGHT_DOWN = GLFW_HAT_RIGHT_DOWN;
		public static final int HAT_LEFT_UP = GLFW_HAT_LEFT_UP;
		public static final int HAT_LEFT_DOWN = GLFW_HAT_LEFT_DOWN;
	}

	public static class Slot {

		public static final byte GAMEPAD_1 = GLFW_JOYSTICK_1;
		public static final byte GAMEPAD_2 = GLFW_JOYSTICK_2;
		public static final byte GAMEPAD_3 = GLFW_JOYSTICK_3;
		public static final byte GAMEPAD_4 = GLFW_JOYSTICK_4;
		public static final byte GAMEPAD_5 = GLFW_JOYSTICK_5;
		public static final byte GAMEPAD_6 = GLFW_JOYSTICK_6;
		public static final byte GAMEPAD_7 = GLFW_JOYSTICK_7;
		public static final byte GAMEPAD_8 = GLFW_JOYSTICK_8;
		public static final byte GAMEPAD_9 = GLFW_JOYSTICK_9;
		public static final byte GAMEPAD_10 = GLFW_JOYSTICK_10;
		public static final byte GAMEPAD_11 = GLFW_JOYSTICK_11;
		public static final byte GAMEPAD_12 = GLFW_JOYSTICK_12;
		public static final byte GAMEPAD_13 = GLFW_JOYSTICK_13;
		public static final byte GAMEPAD_14 = GLFW_JOYSTICK_14;
		public static final byte GAMEPAD_15 = GLFW_JOYSTICK_15;
		public static final byte GAMEPAD_16 = GLFW_JOYSTICK_16;
		public static final byte GAMEPAD_LAST = GLFW_JOYSTICK_LAST;
	}

	public final int slot;

	public Joystick(int slot) {
		this(0, slot);
	}

	protected Joystick(int maxKeys, int slot) {
		super(maxKeys);
		this.slot = slot;
	}

	@Override
	public void poll() {
	}

	@Override
	public boolean isKeyPressed(Key key) {
		return key.getGamepadKey() != Key.KEY_UNKNOWN && isKeyPressed(key.getGamepadKey());
	}

	@Override
	public boolean isKeyPressed(int key) {
		return false;
	}

	@Override
	public boolean isKeyReleased(Key key) {
		return key.getGamepadKey() != Key.KEY_UNKNOWN && isKeyReleased(key.getGamepadKey());
	}

	@Override
	public boolean isKeyReleased(int key) {
		return false;
	}

	@Override
	public String getName() {
		return glfwGetJoystickName(slot);
	}

	@Override
	public boolean isJoystick() {
		return true;
	}

	public FloatBuffer getJoystickAxes() {
		return glfwGetJoystickAxes(slot);
	}

	public ByteBuffer getJoystickButtons() {
		return glfwGetJoystickButtons(slot);
	}

	public ByteBuffer getJoystickHats() {
		return glfwGetJoystickHats(slot);
	}

	public void setJoystickUserPointer(long pointer) {
		glfwSetJoystickUserPointer(slot, pointer);
	}

	public long getJoystickUserPointer() {
		return glfwGetJoystickUserPointer(slot);
	}

	@Override
	public String toString() {
		return super.toString() + " [slot=" + slot + ", name=" + getName() + "]";
	}
}
