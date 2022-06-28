package org.jgine.core.input;

import org.jgine.misc.collection.list.IntList;
import org.jgine.misc.collection.list.arrayList.IntArrayList;
import org.jgine.misc.math.FastMath;

public abstract class InputDevice {

	private IntList pressedKeys;
	private int[] keytimes;

	public InputDevice(int maxKeys) {
		pressedKeys = new IntArrayList(maxKeys);
		keytimes = new int[maxKeys];
	}

	public abstract void poll();

	public abstract boolean isKeyPressed(Key key);

	public abstract boolean isKeyPressed(int key);

	public abstract boolean isKeyReleased(Key key);

	public abstract boolean isKeyReleased(int key);

	public abstract String getName();

	protected final void press(int key) {
		pressedKeys.add(key);
		keytimes[key] = 1;
	}

	protected final void release(int key) {
		keytimes[key] = -keytimes[key];
	}

	protected final void repeat(int key) {
		keytimes[key]++;
	}

	public final IntList getPressedKeys() {
		return pressedKeys;
	}

	public final boolean isPressedIntern(int key) {
		return keytimes[key] > 0;
	}

	public final int getTimePressed(int key) {
		return FastMath.abs(keytimes[key]);
	}

	public boolean isKeyboard() {
		return false;
	}

	public boolean isMouse() {
		return false;
	}

	public boolean isJoystick() {
		return false;
	}

	public boolean isGamepad() {
		return false;
	}
}
