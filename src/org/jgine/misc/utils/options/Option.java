package org.jgine.misc.utils.options;

public class Option {

	public final String key;
	private Object value;

	public Option(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = value;
		OptionFile.setData(key, value);
	}

	public Object getValue() {
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
