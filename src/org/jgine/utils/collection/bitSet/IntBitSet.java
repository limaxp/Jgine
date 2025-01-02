package org.jgine.utils.collection.bitSet;

import java.io.Serializable;
import java.util.BitSet;

/**
 * A {@link BitSet} backed by a int. Can only hold up to 32 bits.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an {@code ArrayList} instance concurrently, and at
 * least one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally. (A structural modification is any operation that
 * adds or deletes one or more elements, or explicitly resizes the backing
 * array; merely setting the value of an element is not a structural
 * modification.) This is typically accomplished by synchronizing on some object
 * that naturally encapsulates the list.
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
		if (bit)
			set(index);
		else
			clear(index);
	}

	public void set(int index) {
		bits |= 1L << index;
	}

	public void clear(int index) {
		bits &= ~(1L << index);
	}

	public void flip(int index) {
		bits ^= 1L << index;
	}

	public boolean get(int index) {
		return (bits & (1L << index)) != 0;
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
