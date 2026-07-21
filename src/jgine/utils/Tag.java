package jgine.utils;

/**
 * A 32 bit flag. Uses prime numbers.
 * <p>
 * <strong>Values can only hold a few flags before integer overflow!</strong>
 * 
 * <pre>
 * multiple Tags -> max unique tags
 *             1 -> 105.097.565
 *             2 -> 46.337
 *             3 -> 210
 *             4 -> 49
 *             5 -> 22
 * </pre>
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
