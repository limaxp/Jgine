package org.jgine.system.systems.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.vector.Vector2f;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class InputHandler implements SystemObject {

	private Entity entity;
	private boolean mouse;
	private boolean keyboard;
	private int gamepad;
	private Int2ObjectMap<Runnable> keyboardMap;
	private Int2ObjectMap<Runnable> mouseMap;
	private Int2ObjectMap<Runnable> gamepadMap;

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
		mouse = true;
		keyboard = true;
		gamepad = 0;
		keyboardMap = new Int2ObjectOpenHashMap<Runnable>();
		mouseMap = new Int2ObjectOpenHashMap<Runnable>();
		gamepadMap = new Int2ObjectOpenHashMap<Runnable>();
	}

	protected void init(Entity entity) {
	}

	protected void checkInput() {
		if (mouse) {
			Mouse mouse = Input.getMouse();
			mouseMove.accept(Input.getCursorPos());
			mouseScroll.accept(mouse.getScroll());
			checkKeys(mouse, mouseMap);
		}

		if (keyboard) {
			checkKeys(Input.getKeyboard(), keyboardMap);
		}

		if (gamepad != -1) {
			Gamepad gamepad = Input.getGamepad(this.gamepad);
			if (gamepad != null) {
				gamepadLeftStickMove.accept(gamepad.getAxisLeft());
				gamepadRightStickMove.accept(gamepad.getAxisRight());
				gamepadLeftTriggerMove.accept(gamepad.getTriggerLeft());
				gamepadRightTriggerMove.accept(gamepad.getTriggerRight());
				checkKeys(gamepad, gamepadMap);
			}
		}
	}

	protected void checkKeys(InputDevice inputDevice, Map<Integer, Runnable> map) {
		for (int pressedKey : inputDevice.getPressedKeys())
			map.getOrDefault(pressedKey, () -> {
			}).run();
	}

	public void load(Map<String, Object> data) {
		Object mouseData = data.get("mouse");
		if (mouseData != null)
			mouse = YamlHelper.toBoolean(mouseData);
		Object keyboardData = data.get("keyboard");
		if (keyboardData != null)
			keyboard = YamlHelper.toBoolean(keyboardData);
		Object gamepadData = data.get("gamepad");
		if (gamepadData != null)
			gamepad = YamlHelper.toInt(gamepadData) - 1;
	}

	public void load(DataInput in) throws IOException {
		mouse = in.readBoolean();
		keyboard = in.readBoolean();
		gamepad = in.readInt();
	}

	public void save(DataOutput out) throws IOException {
		out.writeBoolean(mouse);
		out.writeBoolean(keyboard);
		out.writeInt(gamepad);
	}

	public InputHandlerType<? extends InputHandler> getType() {
		return InputHandlerTypes.UNKNOWN;
	}

	final void setEntity(Entity entity) {
		this.entity = entity;
		init(entity);
	}

	public final Entity getEntity() {
		return entity;
	}

	public final void setMouse(boolean mouse) {
		this.mouse = mouse;
	}

	public final boolean getMouse() {
		return mouse;
	}

	public final void setKeyboard(boolean keyboard) {
		this.keyboard = keyboard;
	}

	public final boolean getKeyboard() {
		return keyboard;
	}

	public final void setGamepad(int gamepad) {
		this.gamepad = gamepad;
	}

	public final int getGamepad() {
		return gamepad;
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

	public final void setMouseScroll(Consumer<Float> mouseScroll) {
		this.mouseScroll = mouseScroll;
	}

	public final Consumer<Float> getMouseScroll() {
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
			object.keyboardMap = new Int2ObjectOpenHashMap<Runnable>(keyboardMap);
			object.mouseMap = new Int2ObjectOpenHashMap<Runnable>(mouseMap);
			object.gamepadMap = new Int2ObjectOpenHashMap<Runnable>(gamepadMap);
			return object;
		} catch (CloneNotSupportedException e) {
			Logger.err("InputHandler: Error on clone!", e);
			return null;
		}
	}
}
