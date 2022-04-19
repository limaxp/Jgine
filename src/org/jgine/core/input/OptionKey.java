package org.jgine.core.input;

import org.jgine.misc.utils.options.Option;
import org.jgine.misc.utils.options.Options;

public class OptionKey extends Key {

	private Option keyboardOption;
	private Option keyboardAltOption;
	private Option gamepadOption;

	public OptionKey(String name, int keyboardKey, int gamepadKey) {
		this(name, keyboardKey, KEY_UNKNOWN, gamepadKey);
	}

	public OptionKey(String name, int keyboardKey, int keyboardAltKey, int gamepadKey) {
		keyboardOption = Options.a("input." + name + ".keyboard", keyboardKey);
		keyboardAltOption = Options.a("input." + name + ".keyboardAlt", keyboardAltKey);
		gamepadOption = Options.a("input." + name + ".gamepad", gamepadKey);
		this.keyboardKey = keyboardOption.getInt();
		this.keyboardAltKey = keyboardAltOption.getInt();
		this.gamepadKey = gamepadOption.getInt();
	}

	@Override
	public void setKeyboardKey(int key) {
		this.keyboardKey = key;
		keyboardOption.setValue(key);
	}

	@Override
	public void setKeyboardAltKey(int key) {
		this.keyboardAltKey = key;
		keyboardAltOption.setValue(key);
	}

	@Override
	public void setGamepadKey(int key) {
		this.gamepadKey = key;
		gamepadOption.setValue(key);
	}
}