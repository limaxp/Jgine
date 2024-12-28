package org.jgine.system.systems.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;
import org.jgine.core.input.device.Gamepad;
import org.jgine.core.input.device.Mouse;
import org.jgine.system.SystemObject;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.vector.Vector2f;

public class InputHandler implements SystemObject {

	private Entity entity;
	protected InputDevice[] inputDevices;
	private Map<Integer, Runnable> keyboardMap;
	private Map<Integer, Runnable> mouseMap;
	private Map<Integer, Runnable> gamepadMap;

	private Consumer<Vector2f> mouseMove = (pos) -> {
	};
	private Consumer<Float> mouseScroll = (scroll) -> {
	};
	private Consumer<Vector2f> gamepadLeftStickMove = (pos) -> {
	};
	private Consumer<Vector2f> gamepadRightStickMove = (pos) -> {
	};
	private Consumer<Float> gamepadLeftTriggerMove = (value) -> {
	};
	private Consumer<Float> gamepadRightTriggerMove = (value) -> {
	};

	public InputHandler() {
		Gamepad gamepad = Input.getGamepad(Gamepad.Slot.GAMEPAD_1);
		if (gamepad != null)
			setInputDevice(new InputDevice[] { Input.getMouse(), Input.getKeyboard(), gamepad });
		else
			setInputDevice(new InputDevice[] { Input.getMouse(), Input.getKeyboard() });

		keyboardMap = new HashMap<Integer, Runnable>();
		mouseMap = new HashMap<Integer, Runnable>();
		gamepadMap = new HashMap<Integer, Runnable>();
	}

	public final void checkInput() {
		for (InputDevice inputDevice : inputDevices)
			checkInput(inputDevice);
	}

	public void checkInput(InputDevice inputDevice) {
		if (inputDevice.isMouse()) {
			Mouse mouse = (Mouse) inputDevice;
			mouseMove.accept(Input.getCursorPos());
			mouseScroll.accept(mouse.getScroll());
			checkKeys(inputDevice, mouseMap);

		} else if (inputDevice.isGamepad()) {
			Gamepad gamepad = (Gamepad) inputDevice;
			gamepadLeftStickMove.accept(gamepad.getAxisLeft());
			gamepadRightStickMove.accept(gamepad.getAxisRight());
			gamepadLeftTriggerMove.accept(gamepad.getTriggerLeft());
			gamepadRightTriggerMove.accept(gamepad.getTriggerRight());
			checkKeys(inputDevice, gamepadMap);

		} else if (inputDevice.isKeyboard()) {
			checkKeys(inputDevice, keyboardMap);
		}
	}

	protected final void checkKeys(InputDevice inputDevice, Map<Integer, Runnable> map) {
		for (int pressedKey : inputDevice.getPressedKeys())
			map.getOrDefault(pressedKey, () -> {
			}).run();
	}

	public InputHandlerType<? extends InputHandler> getType() {
		return InputHandlerTypes.UNKNOWN;
	}

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	public final Entity getEntity() {
		return entity;
	}

	public final void setInputDevice(InputDevice[] inputDevices) {
		this.inputDevices = inputDevices;
	}

	public final InputDevice[] getInputDevices() {
		return inputDevices;
	}

	public final void setKey(Key key, Runnable func) {
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN)
			keyboardMap.put(keyboardKey, func);

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN)
			keyboardMap.put(keyboardAltKey, func);

		int mouseKey = key.getMouseKey();
		if (mouseKey != Key.KEY_UNKNOWN)
			mouseMap.put(mouseKey, func);

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN)
			gamepadMap.put(gamepadKey, func);
	}

	@Nullable
	public final Runnable removeKey(Key key) {
		Runnable result = null;
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN)
			result = keyboardMap.remove(keyboardKey);

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN)
			result = keyboardMap.remove(keyboardAltKey);

		int mouseKey = key.getMouseKey();
		if (mouseKey != Key.KEY_UNKNOWN)
			result = mouseMap.remove(mouseKey);

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN)
			result = gamepadMap.remove(gamepadKey);
		return result;
	}

	@Nullable
	public final Runnable getKey(InputDevice inputDevice, Key key) {
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN)
			return keyboardMap.get(keyboardKey);

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN)
			return keyboardMap.get(keyboardAltKey);

		int mouseKey = key.getMouseKey();
		if (mouseKey != Key.KEY_UNKNOWN)
			return mouseMap.get(mouseKey);

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN)
			return gamepadMap.get(gamepadKey);
		return null;
	}

	public final void clearKeys() {
		keyboardMap.clear();
		mouseMap.clear();
		gamepadMap.clear();
	}

	public final void setMouseMove(Consumer<Vector2f> mouseMove) {
		this.mouseMove = mouseMove;
	}

	public final Consumer<Vector2f> getMouseMove() {
		return mouseMove;
	}

	public void setMouseScroll(Consumer<Float> mouseScroll) {
		this.mouseScroll = mouseScroll;
	}

	public Consumer<Float> getMouseScroll() {
		return mouseScroll;
	}

	public final void setGamepadLeftStickMove(Consumer<Vector2f> gamepadLeftStickMove) {
		this.gamepadLeftStickMove = gamepadLeftStickMove;
	}

	public final Consumer<Vector2f> getGamepadLeftStickMove() {
		return gamepadLeftStickMove;
	}

	public final void setGamepadRightStickMove(Consumer<Vector2f> gamepadRightStickMove) {
		this.gamepadRightStickMove = gamepadRightStickMove;
	}

	public final Consumer<Vector2f> getGamepadRightStickMove() {
		return gamepadRightStickMove;
	}

	@Override
	public InputHandler clone() {
		try {
			InputHandler object = (InputHandler) super.clone();
			object.inputDevices = Arrays.copyOf(inputDevices, inputDevices.length);
			object.keyboardMap = new HashMap<Integer, Runnable>(keyboardMap);
			object.mouseMap = new HashMap<Integer, Runnable>(mouseMap);
			object.gamepadMap = new HashMap<Integer, Runnable>(gamepadMap);
			return object;
		} catch (CloneNotSupportedException e) {
			Logger.err("InputHandler: Error on clone!", e);
			return null;
		}
	}
}
