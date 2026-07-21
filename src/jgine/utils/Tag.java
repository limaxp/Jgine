package jgine.utils;

/**
 * A 32 bit flag. Uses prime numbers. Supports all prime numbers up to
 * Integer.MAX_VALUE. But values can only hold a few flags at once before
 * integer overflow.
 * 
 */
public final class Tag {

	public static int set(int tag, int value, boolean active) {
		return active ? set(tag, value) : clear(tag, value);
	}

	public static int set(int tag, int value) {
		return tag * value;
	}

	public static int clear(int tag, int value) {
		return tag / value;
	}

	public static int flip(int tag, int value) {
		return get(tag, value) ? clear(tag, value) : set(tag, value);
	}

	public static boolean get(int tag, int value) {
		return tag % value == 0;
	}
}
