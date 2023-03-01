package org.jgine.core.input;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwJoystickIsGamepad;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwUpdateGamepadMappings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.list.IntList;
import org.jgine.core.input.device.Gamepad;
import org.jgine.core.input.device.Joystick;
import org.jgine.core.input.device.Keyboard;
import org.jgine.core.input.device.Mouse;
import org.jgine.core.window.Window;
import org.jgine.utils.FileUtils;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.vector.Vector2f;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWJoystickCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

/**
 * Manager that handles input and stores {@link InputDevice}<code>s</code>. Has
 * many helper methods to customize input. Use {@link InputDevice}<code>s</code>
 * to check for clicks, etc.
 */
public class Input {

	private static Window window;
	private static final List<InputDevice> INPUT_DEVICES;
	private static final Mouse MOUSE;
	private static final Keyboard KEYBOARD;
	private static final Joystick[] JOYSTICKS;

	static {
		INPUT_DEVICES = new ArrayList<InputDevice>();
		INPUT_DEVICES.add(MOUSE = new Mouse());
		INPUT_DEVICES.add(KEYBOARD = new Keyboard());

		JOYSTICKS = new Joystick[Joystick.Slot.GAMEPAD_LAST];
		for (int i = 0; i < Gamepad.Slot.GAMEPAD_LAST; i++)
			if (hasJoystick(i))
				registerJoystick(i);

		setJoystickCallback((slot, event) -> {
			if (event == GLFW_CONNECTED)
				registerJoystick(slot);
			else if (event == GLFW_DISCONNECTED)
				unregisterJoystick(slot);
		});

		updateGamepadMapping(Input.class.getResourceAsStream("gamecontrollerdb.txt"));
	}

	public static void updateGamepadMapping(InputStream stream) {
		ByteBuffer buffer;
		try {
			buffer = FileUtils.readByteBuffer(stream);
		} catch (IOException e) {
			Logger.err("Input: Error loading gamepad mapping!", e);
			return;
		}
		buffer.put((byte) 0);
		buffer.flip();
		glfwUpdateGamepadMappings(buffer);
	}

	private static Joystick registerJoystick(int slot) {
		Joystick device;
		if (isGamepad(slot))
			device = new Gamepad(slot);
		else
			device = new Joystick(slot);
		INPUT_DEVICES.add(device);
		JOYSTICKS[slot] = device;
		return device;
	}

	private static Joystick unregisterJoystick(int slot) {
		Joystick device = JOYSTICKS[slot];
		INPUT_DEVICES.remove(device);
		JOYSTICKS[slot] = null;
		return device;
	}

	public static void poll() {
		for (InputDevice device : INPUT_DEVICES)
			device.poll();
	}

	public static void update() {
		for (InputDevice device : INPUT_DEVICES) {
			IntList pressedKeys = device.getPressedKeys();
			for (int i = pressedKeys.size() - 1; i >= 0; i--) {
				int pressedKey = pressedKeys.getInt(i);
				if (!device.isPressedIntern(pressedKey))
					pressedKeys.remove(pressedKeys.indexOf(pressedKey));
			}
		}
		MOUSE.setScroll(0);
	}

	public static List<InputDevice> getDevices() {
		return Collections.unmodifiableList(INPUT_DEVICES);
	}

	public static Mouse getMouse() {
		return MOUSE;
	}

	public static Keyboard getKeyboard() {
		return KEYBOARD;
	}

	@Nullable
	public static Joystick getJoystick(int slot) {
		return JOYSTICKS[slot];
	}

	@Nullable
	public static Gamepad getGamepad(int slot) {
		Joystick joystick = JOYSTICKS[slot];
		if (joystick instanceof Gamepad)
			return (Gamepad) joystick;
		return null;
	}

	public static void setWindow(Window window) {
		Input.window = window;

		setKeyCallback((win, key, scanCode, action, mods) -> {
			if (key == Key.KEY_UNKNOWN)
				return;
			if (action == Key.PRESS)
				KEYBOARD.press(key);
			else if (action == Key.RELEASE)
				KEYBOARD.release(key);
		});

		setMouseButtonCallback((win, button, action, mods) -> {
			if (button == Key.KEY_UNKNOWN)
				return;
			if (action == Key.PRESS)
				MOUSE.press(button);
			else if (action == Key.RELEASE)
				MOUSE.release(button);
		});

		setScrollCallback((win, x, y) -> MOUSE.scroll(y));
	}

	public static Window getWindow() {
		return window;
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

	public static boolean hasJoystick(int slot) {
		return glfwJoystickPresent(slot);
	}

	public static boolean hasGamepad(int slot) {
		return hasJoystick(slot) && isGamepad(slot);
	}

	public static boolean isGamepad(int slot) {
		return glfwJoystickIsGamepad(slot);
	}

	public static void setCursor(Cursor cursor) {
		window.setCursor(cursor);
	}

	public static void setCursor(long cursor) {
		window.setCursor(cursor);
	}

	public static void setDefaultCursor() {
		window.setDefaultCursor();
	}

	public static void setKeyCallback(GLFWKeyCallbackI callback) {
		window.setKeyCallback(callback);
	}

	public static void setCharCallback(GLFWCharCallbackI callback) {
		window.setCharCallback(callback);
	}

	public static void setCursorPosCallback(GLFWCursorPosCallbackI callback) {
		window.setCursorPosCallback(callback);
	}

	public static void setMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
		window.setMouseButtonCallback(callback);
	}

	public static void setCursorEnterCallback(GLFWCursorEnterCallbackI callback) {
		window.setCursorEnterCallback(callback);
	}

	public static void setScrollCallback(GLFWScrollCallbackI callback) {
		window.setScrollCallback(callback);
	}

	public static void setJoystickCallback(GLFWJoystickCallbackI callback) {
		glfwSetJoystickCallback(callback);
	}
}