package jgine.utils;

import jgine.utils.collection.bitSet.IntBitSet;

/**
 * A 32 bit Flag.
 * 
 * <pre>
 * Flags:
 *  0 - DELETE
 *  1 - PAUSE
 * </pre>
 */
public class Flag extends IntBitSet {

	private static final long serialVersionUID = 2689974595690116704L;

	private Flag() {
	}

	public static final byte DELETE = 0;
	public static final byte PAUSE = 1;
}
