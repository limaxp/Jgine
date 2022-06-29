package org.jgine.core.input.device;

import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;

public class Mouse extends InputDevice {

	public Mouse() {
		super(Key.MOUSE_BUTTON_LAST + 1);
	}

	@Override
	public void poll() {
		for (int pressedKey : getPressedKeys())
			repeat(pressedKey);
	}

	@Override
	public boolean isKeyPressed(Key key) {
		return key.getMouseKey() != Key.KEY_UNKNOWN && isKeyPressed(key.getMouseKey());
	}

	@Override
	public boolean isKeyPressed(int key) {
		return Input.getWindow().getMouseButton(key) == Key.PRESS;
	}

	@Override
	public boolean isKeyReleased(Key key) {
		return key.getMouseKey() != Key.KEY_UNKNOWN && isKeyReleased(key.getMouseKey());
	}

	@Override
	public boolean isKeyReleased(int key) {
		return Input.getWindow().getMouseButton(key) == Key.RELEASE;
	}

	@Override
	public String getName() {
		return "mouse";
	}

	@Override
	public boolean isMouse() {
		return true;
	}
}
