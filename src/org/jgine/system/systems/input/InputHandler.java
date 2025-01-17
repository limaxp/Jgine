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
import org.jgine.utils.registry.ClassPathRegistry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class InputHandler implements SystemObject {

	private Entity entity;
	private boolean mouse;
	private boolean keyboard;
	private int gamepad;
	private boolean active;
	private Int2ObjectMap<Consumer<Integer>> pressMap;
	private Int2ObjectMap<Consumer<Integer>> releaseMap;

	private Consumer<Vector2f> mouseMove = (pos) -> {
	};
	private Consumer<Float> mouseScroll = (scroll) -> {
	};
	private Consumer<Vector2f> leftStickMove = (pos) -> {
	};
	private Consumer<Vector2f> rightStickMove = (pos) -> {
	};
	private Consumer<Float> leftTriggerMove = (value) -> {
	};
	private Consumer<Float> rightTriggerMove = (value) -> {
	};

	public InputHandler() {
		mouse = true;
		keyboard = true;
		gamepad = 0;
		active = true;
		pressMap = new Int2ObjectOpenHashMap<Consumer<Integer>>();
		releaseMap = new Int2ObjectOpenHashMap<Consumer<Integer>>();
	}

	protected void init(Entity entity) {
	}

	protected void checkInput() {
		if (!active)
			return;

		if (mouse) {
			Mouse mouse = Input.getMouse();
			mouseMove.accept(Input.getCursorPos());
			mouseScroll.accept(mouse.getScroll());
			checkKeys(mouse);
		}

		if (keyboard) {
			checkKeys(Input.getKeyboard());
		}

		if (gamepad != -1) {
			Gamepad gamepad = Input.getGamepad(this.gamepad);
			if (gamepad != null) {
				leftStickMove.accept(gamepad.getAxisLeft());
				rightStickMove.accept(gamepad.getAxisRight());
				leftTriggerMove.accept(gamepad.getTriggerLeft());
				rightTriggerMove.accept(gamepad.getTriggerRight());
				checkKeys(gamepad);
			}
		}
	}

	protected void checkKeys(InputDevice inputDevice) {
		int deviceType = inputDevice.getType();
		for (int key : inputDevice.getPressedKeys())
			pressMap.getOrDefault(getId(deviceType, key), (time) -> {
			}).accept(inputDevice.getTimePressed(key));

		for (int key : inputDevice.getReleasedKeys())
			releaseMap.getOrDefault(getId(deviceType, key), (time) -> {
			}).accept(inputDevice.getTimePressed(key));
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
		Object activeData = data.get("active");
		if (activeData != null)
			active = YamlHelper.toBoolean(activeData);
	}

	public void load(DataInput in) throws IOException {
		mouse = in.readBoolean();
		keyboard = in.readBoolean();
		gamepad = in.readInt();
		active = in.readBoolean();
	}

	public void save(DataOutput out) throws IOException {
		out.writeBoolean(mouse);
		out.writeBoolean(keyboard);
		out.writeInt(gamepad);
		out.writeBoolean(active);
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

	public final void setActive(boolean active) {
		this.active = active;
	}

	public final boolean isActive() {
		return active;
	}

	public final void press(Key key, Consumer<Integer> func) {
		setKey(pressMap, key, func);
	}

	public final void removePress(Key key) {
		removeKey(pressMap, key);
	}

	public final void release(Key key, Consumer<Integer> func) {
		setKey(releaseMap, key, func);
	}

	public final void removeRelease(Key key) {
		removeKey(releaseMap, key);
	}

	public final void clearKeys() {
		pressMap.clear();
		releaseMap.clear();
	}

	public final void setMouseMove(Consumer<Vector2f> func) {
		this.mouseMove = func;
	}

	public final Consumer<Vector2f> getMouseMove() {
		return mouseMove;
	}

	public final void setMouseScroll(Consumer<Float> func) {
		this.mouseScroll = func;
	}

	public final Consumer<Float> getMouseScroll() {
		return mouseScroll;
	}

	public final void setLeftStickMove(Consumer<Vector2f> func) {
		this.leftStickMove = func;
	}

	public final Consumer<Vector2f> getLeftStickMove() {
		return leftStickMove;
	}

	public final void setRightStickMove(Consumer<Vector2f> func) {
		this.rightStickMove = func;
	}

	public final Consumer<Vector2f> getRightStickMove() {
		return rightStickMove;
	}

	public final void setLeftTriggerMove(Consumer<Float> leftTriggerMove) {
		this.leftTriggerMove = leftTriggerMove;
	}

	public final Consumer<Float> getLeftTriggerMove() {
		return leftTriggerMove;
	}

	public final void setRightTriggerMove(Consumer<Float> rightTriggerMove) {
		this.rightTriggerMove = rightTriggerMove;
	}

	public final Consumer<Float> getRightTriggerMove() {
		return rightTriggerMove;
	}

	@Override
	public InputHandler clone() {
		try {
			InputHandler object = (InputHandler) super.clone();
			object.pressMap = new Int2ObjectOpenHashMap<Consumer<Integer>>(pressMap);
			object.releaseMap = new Int2ObjectOpenHashMap<Consumer<Integer>>(releaseMap);
			return object;
		} catch (CloneNotSupportedException e) {
			Logger.err("InputHandler: Error on clone!", e);
			return null;
		}
	}

	@Nullable
	public static InputHandler get(String name) {
		return ClassPathRegistry.getInput(name);
	}

	public static void setKey(Int2ObjectMap<Consumer<Integer>> map, Key key, Consumer<Integer> func) {
		int k = key.getKeyboardKey();
		if (k != Key.KEY_UNKNOWN)
			map.put(getId(InputDevice.Type.KEYBOARD, k), func);

		k = key.getKeyboardAltKey();
		if (k != Key.KEY_UNKNOWN)
			map.put(getId(InputDevice.Type.KEYBOARD, k), func);

		k = key.getMouseKey();
		if (k != Key.KEY_UNKNOWN)
			map.put(getId(InputDevice.Type.MOUSE, k), func);

		k = key.getGamepadKey();
		if (k != Key.KEY_UNKNOWN)
			map.put(getId(InputDevice.Type.JOYSTICK, k), func);
	}

	public static void removeKey(Int2ObjectMap<Consumer<Integer>> map, Key key) {
		int k = key.getKeyboardKey();
		if (k != Key.KEY_UNKNOWN)
			map.remove(getId(InputDevice.Type.KEYBOARD, k));

		k = key.getKeyboardAltKey();
		if (k != Key.KEY_UNKNOWN)
			map.remove(getId(InputDevice.Type.KEYBOARD, k));

		k = key.getMouseKey();
		if (k != Key.KEY_UNKNOWN)
			map.remove(getId(InputDevice.Type.MOUSE, k));

		k = key.getGamepadKey();
		if (k != Key.KEY_UNKNOWN)
			map.remove(getId(InputDevice.Type.JOYSTICK, k));
	}

	private static int getId(int deviceType, int key) {
		return 0x00000000 | deviceType << 24 | key;
	}
}
