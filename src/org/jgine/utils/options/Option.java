package org.jgine.utils.options;

import java.util.function.Consumer;

/**
 * An option with a <code>string</code> key and any value. Changing an option
 * will update config.ini file!
 */
public class Option<T extends Object> {

	public final String key;
	private T value;
	private final Consumer<T> func;

	public Option(String key, T value) {
		this(key, value, (v) -> {
		});
	}

	public Option(String key, T value, Consumer<T> func) {
		this.key = key;
		this.value = value;
		this.func = func;
	}

	public void setValue(T value) {
		this.value = value;
		func.accept(value);
		OptionFile.setData(key, value);
	}

	public T getValue() {
		return value;
	}

	public boolean getBoolean() {
		return (boolean) value;
	}

	public short getShort() {
		return (short) value;
	}

	public int getInt() {
		return (int) value;
	}

	public long getLong() {
		return (long) value;
	}

	public float getFloat() {
		return (float) value;
	}

	public double getDouble() {
		return (double) value;
	}

	public char getChar() {
		return (char) value;
	}

	public String getString() {
		return (String) value;
	}
}
