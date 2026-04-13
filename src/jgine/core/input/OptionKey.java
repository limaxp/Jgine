package jgine.core.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jgine.utils.Options;
import jgine.utils.Options.Option;

/**
 * This class extends the {@link Key} class with {@link Option} functionality.
 */
public class OptionKey extends Key {

	private static final List<OptionKey> OPTION_KEY_LIST = new ArrayList<OptionKey>();

	public final String name;
	private Option<Integer> keyboardOption;
	private Option<Integer> keyboardAltOption;
	private Option<Integer> mouseOption;
	private Option<Integer> gamepadOption;

	public OptionKey(String name, int keyboardKey, int keyboardAltKey, int mouseKey, int gamepadKey) {
		this.name = name;
		keyboardOption = Options.as("input." + name + ".keyboard", keyboardKey);
		keyboardAltOption = Options.as("input." + name + ".keyboardAlt", keyboardAltKey);
		mouseOption = Options.as("input." + name + ".mouse", mouseKey);
		gamepadOption = Options.as("input." + name + ".gamepad", gamepadKey);
		this.keyboardKey = keyboardOption.asInt();
		this.keyboardAltKey = keyboardAltOption.asInt();
		this.mouseKey = mouseOption.asInt();
		this.gamepadKey = gamepadOption.asInt();
	}

	@Override
	public void setKeyboardKey(int key) {
		super.setKeyboardKey(key);
		keyboardOption.set(key);
	}

	@Override
	public void setKeyboardAltKey(int key) {
		super.setKeyboardAltKey(key);
		keyboardAltOption.set(key);
	}

	@Override
	public void setMouseKey(int key) {
		super.setMouseKey(key);
		mouseOption.set(key);
	}

	@Override
	public void setGamepadKey(int key) {
		super.setGamepadKey(key);
		gamepadOption.set(key);
	}

	public static List<OptionKey> values() {
		return Collections.unmodifiableList(OPTION_KEY_LIST);
	}
}