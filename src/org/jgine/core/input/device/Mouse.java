package org.jgine.core.input.device;

import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;

public class Mouse implements InputDevice {

	@Override
	public void update() {
	}

	@Override
	public boolean isKeyPressed(Key key) {
		return isKeyPressed(key.getKeyboardKey());
	}

	@Override
	public boolean isKeyPressed(int key) {
		return Input.getWindow().getMouseButton(key) == Key.PRESS;
	}

	@Override
	public boolean isKeyReleased(Key key) {
		return isKeyReleased(key.getKeyboardKey());
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