package org.jgine.core.input.device;

import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;

public class Keyboard implements InputDevice {

	@Override
	public void update() {
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
		return isKeyReleased(key.getKeyboardKey()) || isKeyReleased(key.getKeyboardAltKey());
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
