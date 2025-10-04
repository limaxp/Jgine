package org.jgine.utils.collection.bitSet;

import java.io.Serializable;
import java.util.BitSet;

/**
 * A {@link BitSet} backed by a int. Can only hold up to 32 bits.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 */
public class IntBitSet implements Cloneable, Serializable {

	private static final long serialVersionUID = 9031893883463019904L;

	public static final int MAX_SIZE = 32;

	private int bits;

	public IntBitSet() {
	}

	public IntBitSet(int bits) {
		this.bits = bits;
	}

	public void setBits(int bits) {
		this.bits = bits;
	}

	public int getBits() {
		return bits;
	}

	public void set(int index, boolean bit) {
		bits = set(bits, index, bit);
	}

	public void set(int index) {
		bits = set(bits, index);
	}

	public void clear(int index) {
		bits = clear(bits, index);
	}

	public void flip(int index) {
		bits = flip(bits, index);
	}

	public boolean get(int index) {
		return get(bits, index);
	}

	public void clear() {
		bits = 0;
	}

	public void and(IntBitSet bits) {
		and(bits.getBits());
	}

	public void and(int bits) {
		this.bits &= bits;
	}

	public void andNot(IntBitSet bits) {
		andNot(bits.getBits());
	}

	public void andNot(int bits) {
		this.bits &= ~bits;
	}

	public void or(int bits) {
		this.bits |= bits;
	}

	public void xor(int bits) {
		this.bits ^= bits;
	}

	public void intersects(IntBitSet bits) {
		intersects(bits.getBits());
	}

	public boolean intersects(int bits) {
		return (this.bits & bits) != 0;
	}

	@Override
	public Object clone() {
		try {
			IntBitSet bs = (IntBitSet) super.clone();
			bs.bits = bits;
			return bs;
		} catch (CloneNotSupportedException e) {
			// Impossible to get here.
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntBitSet))
			return false;
		if (bits != ((IntBitSet) obj).bits)
			return false;
		return true;
	}

	public boolean isEmpty() {
		return bits == 0;
	}

	@Override
	public String toString() {
		return "IntBitSet" + toString(bits);
	}

	public static int set(int bits, int index, boolean bit) {
		return bit ? set(bits, index) : clear(bits, index);
	}

	public static int set(int bits, int index) {
		return bits |= 1L << index;
	}

	public static int clear(int bits, int index) {
		return bits &= ~(1L << index);
	}

	public static int flip(int bits, int index) {
		return bits ^= 1L << index;
	}

	public static boolean get(int bits, int index) {
		return (bits & (1L << index)) != 0;
	}

	public static int and(int bits1, int bits2) {
		return bits1 &= bits2;
	}

	public static int andNot(int bits1, int bits2) {
		return bits1 &= ~bits2;
	}

	public static int or(int bits1, int bits2) {
		return bits1 |= bits2;
	}

	public static int xor(int bits1, int bits2) {
		return bits1 ^= bits2;
	}

	public static boolean intersects(int bits1, int bits2) {
		return (bits1 & bits2) != 0;
	}

	public String toString(int bits) {
		StringBuffer stringBuffer = new StringBuffer("[");
		boolean first = true;
		int bit = 1;
		for (int i = 0; i < MAX_SIZE; ++i) {
			if ((bits & bit) != 0) {
				if (!first)
					stringBuffer.append(", ");
				stringBuffer.append(i);
				first = false;
			}
			bit <<= 1;
		}
		return stringBuffer.append("]").toString();
	}
}
