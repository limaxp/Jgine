package jgine.core.input;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.glfwJoystickIsGamepad;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwUpdateGamepadMappings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntList;
import jgine.core.input.device.Gamepad;
import jgine.core.input.device.Joystick;
import jgine.core.input.device.Keyboard;
import jgine.core.input.device.Mouse;
import jgine.core.window.Window;
import jgine.utils.FileUtils;
import jgine.utils.Logger;
import jgine.utils.collection.list.IdentityArrayList;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector2i;
import jgine.utils.scheduler.Scheduler;

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
		INPUT_DEVICES = new IdentityArrayList<InputDevice>(Joystick.Slot.GAMEPAD_SIZE + 2);
		INPUT_DEVICES.add(MOUSE = new Mouse());
		INPUT_DEVICES.add(KEYBOARD = new Keyboard());

		JOYSTICKS = new Joystick[Joystick.Slot.GAMEPAD_SIZE];
		for (int i = 0; i < Joystick.Slot.GAMEPAD_SIZE; i++)
			if (hasJoystick(i))
				registerJoystick(i);

		setJoystickCallback((slot, event) -> {
			if (event == GLFW_CONNECTED)
				registerJoystick(slot);
			else if (event == GLFW_DISCONNECTED)
				unregisterJoystick(slot);
		});

		updateGamepadMapping(FileUtils.getResourceStream("assets/gamecontrollerdb.txt"));
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
			IntList releasedKeys = device.getReleasedKeys();
			releasedKeys.clear();

			for (int i = pressedKeys.size() - 1; i >= 0; i--) {
				int key = pressedKeys.getInt(i);
				if (!device.isPressedIntern(key)) {
					pressedKeys.removeInt(i);
					releasedKeys.add(key);
				} else
					device.tick(key);
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

	@SuppressWarnings("unused")
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

	public static Vector2i getWindowSize() {
		return window.getSize();
	}

	public static int getKey(int key) {
		return window.getKey(key);
	}

	public static int getMouseButton(int key) {
		return window.getMouseButton(key);
	}

	public static void setCursorPos(double x, double y) {
		Scheduler.runTask(() -> window.setCursorPos(x, y));
	}

	public static void setCursorPos(Vector2f vector) {
		Scheduler.runTask(() -> window.setCursorPos(vector));
	}

	public static Vector2f getCursorPos() {
		return window.getCursorPos();
	}

	public static void hideCursor() {
		Scheduler.runTask(window::hideCursor);
	}

	public static void showCursor() {
		Scheduler.runTask(window::showCursor);
	}

	public static void disableCursor() {
		Scheduler.runTask(window::disableCursor);
	}

	public static void enableRawMouseMotion() {
		Scheduler.runTask(window::enableRawMouseMotion);
	}

	public static void disableRawMouseMotion() {
		Scheduler.runTask(window::disableRawMouseMotion);
	}

	public static void toggleRawMouseMotion() {
		Scheduler.runTask(window::toggleRawMouseMotion);
	}

	public static void enableStickyKeys() {
		Scheduler.runTask(window::enableStickyKeys);
	}

	public static void disableStickyKeys() {
		Scheduler.runTask(window::disableStickyKeys);
	}

	public static void toggleStickyKeys() {
		Scheduler.runTask(window::toggleStickyKeys);
	}

	public static void enableStickyMouseButtons() {
		Scheduler.runTask(window::enableStickyMouseButtons);
	}

	public static void disableStickyMouseButtons() {
		Scheduler.runTask(window::disableStickyMouseButtons);
	}

	public static void toggleStickyMouseButtons() {
		Scheduler.runTask(window::toggleStickyMouseButtons);
	}

	public static void enableLockKeyMods() {
		Scheduler.runTask(window::enableLockKeyMods);
	}

	public static void disableLockKeyMods() {
		Scheduler.runTask(window::disableLockKeyMods);
	}

	public static void toggleLockKeyMods() {
		Scheduler.runTask(window::toggleLockKeyMods);
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
		Scheduler.runTask(() -> window.setCursor(cursor));
	}

	public static void setCursor(long cursor) {
		Scheduler.runTask(() -> window.setCursor(cursor));
	}

	public static void setDefaultCursor() {
		Scheduler.runTask(window::setDefaultCursor);
	}

	public static void setKeyCallback(@Nullable KeyCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setKeyCallback(null);
			else
				window.setKeyCallback(callback::invoke);
		});
	}

	public static void setCharCallback(@Nullable CharCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setCharCallback(null);
			else
				window.setCharCallback(callback::invoke);
		});
	}

	public static void setCursorPosCallback(@Nullable CursorPosCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setCursorPosCallback(null);
			else
				window.setCursorPosCallback(callback::invoke);
		});
	}

	public static void setMouseButtonCallback(@Nullable MouseButtonCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setMouseButtonCallback(null);
			else
				window.setMouseButtonCallback(callback::invoke);
		});
	}

	public static void setCursorEnterCallback(@Nullable CursorEnterCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setCursorEnterCallback(null);
			else
				window.setCursorEnterCallback(callback::invoke);
		});
	}

	public static void setScrollCallback(@Nullable ScrollCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				window.setScrollCallback(null);
			else
				window.setScrollCallback(callback::invoke);
		});
	}

	public static void setJoystickCallback(@Nullable JoystickCallback callback) {
		Scheduler.runTask(() -> {
			if (callback == null)
				glfwSetJoystickCallback(null);
			else
				glfwSetJoystickCallback(callback::invoke);
		});
	}

	@FunctionalInterface
	public static interface KeyCallback {

		public void invoke(long window, int key, int scancode, int action, int mods);
	}

	@FunctionalInterface
	public static interface CharCallback {

		public void invoke(long window, int codePoint);
	}

	@FunctionalInterface
	public static interface CursorPosCallback {

		public void invoke(long window, double xPos, double yPos);
	}

	@FunctionalInterface
	public static interface MouseButtonCallback {

		public void invoke(long window, int button, int action, int mods);
	}

	@FunctionalInterface
	public static interface CursorEnterCallback {

		public void invoke(long window, boolean entered);
	}

	@FunctionalInterface
	public static interface ScrollCallback {

		public void invoke(long window, double xoffset, double yoffset);
	}

	@FunctionalInterface
	public static interface JoystickCallback {

		public void invoke(int slot, int event);
	}
}