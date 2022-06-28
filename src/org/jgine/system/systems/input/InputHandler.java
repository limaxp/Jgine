package org.jgine.system.systems.input;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;
import org.jgine.core.input.device.Gamepad;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.system.SystemObject;

public abstract class InputHandler implements SystemObject {

	private Entity entity;
	protected InputDevice[] inputDevices;
	private Map<Integer, Runnable>[] keyMaps;
	private Consumer<Vector2f> mouseMove = (pos) -> {
	};
	private Consumer<Vector2f> gamepadLeftStickMove = (pos) -> {
	};
	private Consumer<Vector2f> gamepadRightStickMove = (pos) -> {
	};

	protected InputHandler() {
		Gamepad gamepad = Input.getGamepad(Gamepad.Slot.GAMEPAD_1);
		if (gamepad != null)
			setInputDevice(new InputDevice[] { Input.getMouse(), Input.getKeyboard(), gamepad });
		else
			setInputDevice(new InputDevice[] { Input.getMouse(), Input.getKeyboard() });
	}

	public final void checkInput() {
		for (InputDevice inputDevice : inputDevices)
			checkInput(inputDevice);
	}

	public void checkInput(InputDevice inputDevice) {
		if (inputDevice.isMouse()) {
			mouseMove.accept(Input.getCursorPos());

		} else if (inputDevice.isGamepad()) {
			Gamepad gamepad = (Gamepad) inputDevice;
			gamepadLeftStickMove.accept(gamepad.getAxisLeft());
			gamepadRightStickMove.accept(gamepad.getAxisRight());
		}

		for (int i = 0; i < inputDevices.length; i++) {
			if (inputDevices[i] == inputDevice) {
				Map<Integer, Runnable> keyMap = keyMaps[i];
				for (int pressedKey : inputDevice.getPressedKeys())
					keyMap.getOrDefault(pressedKey, () -> {
					}).run();
				break;
			}
		}
	}

	public abstract InputHandlerType<?> getType();

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	public final Entity getEntity() {
		return entity;
	}

	@SuppressWarnings("unchecked")
	public final void setInputDevice(InputDevice[] inputDevices) {
		this.inputDevices = inputDevices;
		keyMaps = new HashMap[inputDevices.length];
		for (int i = 0; i < inputDevices.length; i++)
			keyMaps[i] = new HashMap<Integer, Runnable>();
	}

	public final InputDevice[] getInputDevices() {
		return inputDevices;
	}

	public final void setKey(Key key, Runnable func) {
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					keyMaps[i].put(keyboardKey, func);
		}

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					keyMaps[i].put(keyboardAltKey, func);
		}

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isGamepad())
					keyMaps[i].put(gamepadKey, func);
		}
	}

	@Nullable
	public final Runnable removeKey(Key key) {
		Runnable result = null;
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					result = keyMaps[i].remove(keyboardKey);
		}

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					result = keyMaps[i].remove(keyboardAltKey);
		}

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isGamepad())
					result = keyMaps[i].remove(gamepadKey);
		}
		return result;
	}

	@Nullable
	public final Runnable getKey(InputDevice inputDevice, Key key) {
		int keyboardKey = key.getKeyboardKey();
		if (keyboardKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					return keyMaps[i].get(keyboardKey);
		}

		int keyboardAltKey = key.getKeyboardAltKey();
		if (keyboardAltKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isKeyboard())
					return keyMaps[i].get(keyboardAltKey);
		}

		int gamepadKey = key.getGamepadKey();
		if (gamepadKey != Key.KEY_UNKNOWN) {
			for (int i = 0; i < inputDevices.length; i++)
				if (inputDevices[i].isGamepad())
					return keyMaps[i].get(gamepadKey);
		}
		return null;
	}

	public final void setMouseMove(Consumer<Vector2f> mouseMove) {
		this.mouseMove = mouseMove;
	}

	public final Consumer<Vector2f> getMouseMove() {
		return mouseMove;
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
}
