package org.jgine.core.input;

public interface InputDevice {

	public abstract void update();

	public abstract boolean isKeyPressed(Key key);

	public abstract boolean isKeyPressed(int key);

	public abstract boolean isKeyReleased(Key key);

	public abstract boolean isKeyReleased(int key);

	public abstract String getName();

	public default boolean isKeyboard() {
		return false;
	}

	public default boolean isMouse() {
		return false;
	}

	public default boolean isJoystick() {
		return false;
	}

	public default boolean isGamepad() {
		return false;
	}
}
