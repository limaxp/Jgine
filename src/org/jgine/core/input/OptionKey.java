package org.jgine.core.input;

import org.jgine.misc.utils.options.Option;
import org.jgine.misc.utils.options.Options;

public class OptionKey extends Key {

	private Option keyboardOption;
	private Option keyboardAltOption;
	private Option mouseOption;
	private Option gamepadOption;

	public OptionKey(String name, int keyboardKey, int keyboardAltKey, int mouseKey, int gamepadKey) {
		keyboardOption = Options.a("input." + name + ".keyboard", keyboardKey);
		keyboardAltOption = Options.a("input." + name + ".keyboardAlt", keyboardAltKey);
		mouseOption = Options.a("input." + name + ".mouse", mouseKey);
		gamepadOption = Options.a("input." + name + ".gamepad", gamepadKey);
		this.keyboardKey = keyboardOption.getInt();
		this.keyboardAltKey = keyboardAltOption.getInt();
		this.mouseKey = mouseOption.getInt();
		this.gamepadKey = gamepadOption.getInt();
	}

	@Override
	public void setKeyboardKey(int key) {
		super.setKeyboardKey(key);
		keyboardOption.setValue(key);
	}

	@Override
	public void setKeyboardAltKey(int key) {
		super.setKeyboardAltKey(key);
		keyboardAltOption.setValue(key);
	}

	@Override
	public void setMouseKey(int key) {
		super.setMouseKey(key);
		mouseOption.setValue(key);
	}

	@Override
	public void setGamepadKey(int key) {
		super.setGamepadKey(key);
		gamepadOption.setValue(key);
	}
}