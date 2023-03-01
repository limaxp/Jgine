package org.jgine.core.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgine.utils.options.Option;
import org.jgine.utils.options.Options;

/**
 * This class extends the {@link Key} class with {@link Option} functionality.
 */
public class OptionKey extends Key {

	private static final List<OptionKey> OPTION_KEY_LIST = new ArrayList<OptionKey>();

	public final String name;
	private Option keyboardOption;
	private Option keyboardAltOption;
	private Option mouseOption;
	private Option gamepadOption;

	public OptionKey(String name, int keyboardKey, int keyboardAltKey, int mouseKey, int gamepadKey) {
		this.name = name;
		keyboardOption = Options.a("input." + name + ".keyboard", keyboardKey);
		keyboardAltOption = Options.a("input." + name + ".keyboardAlt", keyboardAltKey);
		mouseOption = Options.a("input." + name + ".mouse", mouseKey);
		gamepadOption = Options.a("input." + name + ".gamepad", gamepadKey);
		this.keyboardKey = keyboardOption.getInt();
		this.keyboardAltKey = keyboardAltOption.getInt();
		this.mouseKey = mouseOption.getInt();
		this.gamepadKey = gamepadOption.getInt();
		OPTION_KEY_LIST.add(this);
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

	public static List<OptionKey> getList() {
		return Collections.unmodifiableList(OPTION_KEY_LIST);
	}
}