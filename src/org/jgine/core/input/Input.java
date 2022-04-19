package org.jgine.core.input;

import static org.lwjgl.glfw.GLFW.*;
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
import static org.lwjgl.glfw.GLFW.glfwGetGamepadName;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickAxes;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickButtons;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickHats;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickUserPointer;
import static org.lwjgl.glfw.GLFW.glfwJoystickIsGamepad;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickUserPointer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.jgine.core.Window;
import org.jgine.core.Window.CharCallback;
import org.jgine.core.Window.CursorEnterCallback;
import org.jgine.core.Window.CursorPosCallback;
import org.jgine.core.Window.KeyCallback;
import org.jgine.core.Window.MouseButtonCallback;
import org.jgine.core.Window.ScrollCallback;
import org.jgine.misc.math.vector.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

public class Input {

	public static class Slot {

		public static final byte KEYBOARD = -1;
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

	public static class JoystickHats {

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

	private static Window window;
	private static GamepadState[] gamepadStates;

	static {
		gamepadStates = new GamepadState[Slot.GAMEPAD_LAST];
		for (int i = 0; i < Slot.GAMEPAD_LAST; i++)
			gamepadStates[i] = new GamepadState(BufferUtils.createByteBuffer(GamepadState.SIZEOF));
	}

	public static void setWindow(Window window) {
		Input.window = window;
	}

	public static void update() {
		for (int i = 0; i < Slot.GAMEPAD_LAST; i++) {
			if (hasGamepad(i))
				glfwGetGamepadState(i, gamepadStates[i]);
		}
	}

	public static boolean isKeyPressed(byte inputSlot, Key key) {
		if (inputSlot == Slot.KEYBOARD)
			return isKeyPressed(key);
		else if (key.gamepadKey != Key.UNKNOWN)
			return isGamepadPressed(inputSlot, key.gamepadKey);
		return false;
	}

	public static boolean isKeyReleased(byte inputSlot, Key key) {
		if (inputSlot == Slot.KEYBOARD)
			return isKeyReleased(key);
		else if (key.gamepadKey != Key.UNKNOWN)
			return isGamepadReleased(inputSlot, key.gamepadKey);
		return false;
	}

	public static boolean isKeyPressed(Key key) {
		return (key.keyboardKey != Key.UNKNOWN && isKeyPressed(key.keyboardKey)) ||
				(key.keyboardAltKey != Key.UNKNOWN && isKeyPressed(key.keyboardAltKey));
	}

	public static boolean isKeyReleased(Key key) {
		return (key.keyboardKey != Key.UNKNOWN && isKeyReleased(key.keyboardKey)) ||
				(key.keyboardAltKey != Key.UNKNOWN && isKeyReleased(key.keyboardAltKey));
	}

	public static boolean isKeyPressed(int key) {
		return window.getKey(key) == GLFW.GLFW_PRESS;
	}

	public static boolean isKeyReleased(int key) {
		return window.getKey(key) == GLFW.GLFW_RELEASE;
	}

	public static boolean isGamepadPressed(byte inputSlot, int key) {
		return getGamepadState(inputSlot).buttons(key) == 1;
	}

	public static boolean isGamepadReleased(byte inputSlot, int key) {
		return getGamepadState(inputSlot).buttons(key) == 0;
	}

	public static boolean isMousePressed(int key) {
		return window.getMouseButton(key) == GLFW.GLFW_PRESS;
	}

	public static boolean isMouseReleased(int key) {
		return window.getMouseButton(key) == GLFW.GLFW_RELEASE;
	}

	public static void setCursorPos(double x, double y) {
		window.setCursorPos(x, y);
	}

	public static void setCursorPos(Vector2f vector) {
		window.setCursorPos(vector);
	}

	public static Vector2f getCursorPos() {
		return window.getCursorPos();
	}

	public static void hideCursor() {
		window.hideCursor();
	}

	public static void showCursor() {
		window.showCursor();
	}

	public static void disableCursor() {
		window.disableCursor();
	}

	public static void enableRawMouseMotion() {
		window.enableRawMouseMotion();
	}

	public static void disableRawMouseMotion() {
		window.disableRawMouseMotion();
	}

	public static void toggleRawMouseMotion() {
		window.toggleRawMouseMotion();
	}

	public static void enableStickyKeys() {
		window.enableStickyKeys();
	}

	public static void disableStickyKeys() {
		window.disableStickyKeys();
	}

	public static void toggleStickyKeys() {
		window.toggleStickyKeys();
	}

	public static void enableStickyMouseButtons() {
		window.enableStickyMouseButtons();
	}

	public static void disableStickyMouseButtons() {
		window.disableStickyMouseButtons();
	}

	public static void toggleStickyMouseButtons() {
		window.toggleStickyMouseButtons();
	}

	public static void enableLockKeyMods() {
		window.enableLockKeyMods();
	}

	public static void disableLockKeyMods() {
		window.disableLockKeyMods();
	}

	public static void toggleLockKeyMods() {
		window.toggleLockKeyMods();
	}

	public static void setKeyCallback(KeyCallback callback) {
		window.setKeyCallback(callback);
	}

	public static void setCharCallback(CharCallback callback) {
		window.setCharCallback(callback);
	}

	public static void setCursorPosCallback(CursorPosCallback callback) {
		window.setCursorPosCallback(callback);
	}

	public static void setMouseButtonCallback(MouseButtonCallback callback) {
		window.setMouseButtonCallback(callback);
	}

	public void setCursorEnterCallback(CursorEnterCallback callback) {
		window.setCursorEnterCallback(callback);
	}

	public void setScrollCallback(ScrollCallback callback) {
		window.setScrollCallback(callback);
	}

	public static boolean hasJoystick(int index) {
		return glfwJoystickPresent(index);
	}

	public static boolean hasGamepad(int index) {
		return glfwJoystickIsGamepad(index);
	}

	public static String getJoystickName(int index) {
		return glfwGetJoystickName(index);
	}

	public static String getGamepadName(int index) {
		return glfwGetGamepadName(index);
	}

	public static GamepadState getGamepadState(int index) {
		return gamepadStates[index];
	}

	public static FloatBuffer getJoystickAxes(int index) {
		return glfwGetJoystickAxes(index);
	}

	public static ByteBuffer getJoystickButtons(int index) {
		return glfwGetJoystickButtons(index);
	}

	public static ByteBuffer getJoystickHats(int index) {
		return glfwGetJoystickHats(index);
	}

	public static void setJoystickUserPointer(int index, long pointer) {
		glfwSetJoystickUserPointer(index, pointer);
	}

	public static long getJoystickUserPointer(int index) {
		return glfwGetJoystickUserPointer(index);
	}

	public static void setJoystickCallback(JoyStickCallback callback) {
		glfwSetJoystickCallback(callback);
	}

	@FunctionalInterface
	public static interface JoyStickCallback extends GLFWJoystickCallbackI {}

	public static class GamepadState extends GLFWGamepadState {

		public GamepadState(ByteBuffer container) {
			super(container);
		}
	}
}