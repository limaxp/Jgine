package org.jgine.collection.bitSet;

import java.io.Serializable;
import java.util.BitSet;

/**
 * A {@link BitSet} backed by a long. Can only hold up to 64 bits.
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
public class LongBitSet implements Cloneable, Serializable {

	private static final long serialVersionUID = 9031893883463019904L;

	public static final int MAX_SIZE = 64;

	private long bits;

	public LongBitSet() {
	}

	public LongBitSet(long bits) {
		this.bits = bits;
	}

	public void setBits(long bits) {
		this.bits = bits;
	}

	public long getBits() {
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

	public void and(LongBitSet bits) {
		and(bits.getBits());
	}

	public void and(long bits) {
		this.bits &= bits;
	}

	public void andNot(LongBitSet bits) {
		andNot(bits.getBits());
	}

	public void andNot(long bits) {
		this.bits &= ~bits;
	}

	public void or(long bits) {
		this.bits |= bits;
	}

	public void xor(long bits) {
		this.bits ^= bits;
	}

	public int cardinality() {
		return cardinality(bits);
	}

	public void intersects(LongBitSet bits) {
		intersects(bits.getBits());
	}

	public boolean intersects(long bits) {
		return (this.bits & bits) != 0;
	}

	@Override
	public Object clone() {
		try {
			LongBitSet bs = (LongBitSet) super.clone();
			bs.bits = bits;
			return bs;
		} catch (CloneNotSupportedException e) {
			// Impossible to get here.
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LongBitSet))
			return false;
		if (bits != ((LongBitSet) obj).bits)
			return false;
		return true;
	}

	public boolean isEmpty() {
		return bits == 0;
	}

	@Override
	public String toString() {
		return "LongBitSet" + toString(bits);
	}

	public static long set(long bits, int index, boolean bit) {
		return bit ? set(bits, index) : clear(bits, index);
	}

	public static long set(long bits, int index) {
		return bits |= 1L << index;
	}

	public static long clear(long bits, int index) {
		return bits &= ~(1L << index);
	}

	public static long flip(long bits, int index) {
		return bits ^= 1L << index;
	}

	public static boolean get(long bits, int index) {
		return (bits & (1L << index)) != 0;
	}

	public static long and(long bits1, long bits2) {
		return bits1 &= bits2;
	}

	public static long andNot(long bits1, long bits2) {
		return bits1 &= ~bits2;
	}

	public static long or(long bits1, long bits2) {
		return bits1 |= bits2;
	}

	public static long xor(long bits1, long bits2) {
		return bits1 ^= bits2;
	}

	public static int cardinality(long bits) {
		if (bits == 0)
			return 0;

		// Successively collapse alternating bit groups into a sum.
		bits = ((bits >> 1) & 0x5555555555555555L) + (bits & 0x5555555555555555L);
		bits = ((bits >> 2) & 0x3333333333333333L) + (bits & 0x3333333333333333L);
		int b = (int) ((bits >>> 32) + bits);
		b = ((b >> 4) & 0x0f0f0f0f) + (b & 0x0f0f0f0f);
		b = ((b >> 8) & 0x00ff00ff) + (b & 0x00ff00ff);
		return ((b >> 16) & 0x0000ffff) + (b & 0x0000ffff);
	}

	public static boolean intersects(long bits1, long bits2) {
		return (bits1 & bits2) != 0;
	}

	public String toString(long bits) {
		StringBuffer stringBuffer = new StringBuffer("[");
		boolean first = true;
		long bit = 1;
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
