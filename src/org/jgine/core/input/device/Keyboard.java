package org.jgine.core.input.device;

import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;

public class Keyboard extends InputDevice {

	public Keyboard() {
		super(Key.KEY_LAST + 1);
	}

	@Override
	public void poll() {
		for (int pressedKey : getPressedKeys())
			repeat(pressedKey);
	}

	@Override
	public boolean isKeyPressed(Key key) {
		return isKeyPressed(key.getKeyboardKey())
				|| (key.getKeyboardAltKey() != Key.KEY_UNKNOWN && isKeyPressed(key.getKeyboardAltKey()));
	}

	@Override
	public boolean isKeyPressed(int key) {
		return Input.getWindow().getKey(key) == Key.PRESS;
	}

	@Override
	public boolean isKeyReleased(Key key) {
		return isKeyReleased(key.getKeyboardKey())
				|| (key.getKeyboardAltKey() != Key.KEY_UNKNOWN && isKeyReleased(key.getKeyboardAltKey()));
	}

	@Override
	public boolean isKeyReleased(int key) {
		return Input.getWindow().getKey(key) == Key.RELEASE;
	}

	@Override
	public String getName() {
		return "keyboard";
	}

	@Override
	public boolean isKeyboard() {
		return true;
	}
}
