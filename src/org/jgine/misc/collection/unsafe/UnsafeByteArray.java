package org.jgine.misc.collection.unsafe;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;
import org.jgine.misc.utils.memory.MemoryHelper;

@SuppressWarnings("restriction")
public class UnsafeByteArray implements AutoCloseable {

	private final Allocator allocator;
	public final long address;
	public final long length;

	public UnsafeByteArray(long size) {
		this(Allocator.DEFAULT_ALLOCATOR, size);
	}

	public UnsafeByteArray(Allocator allocator, long size) {
		this.allocator = allocator;
		this.length = size;
		address = allocator.alloc(size).address;
		MemoryHelper.UNSAFE.setMemory(address, size, (byte) 0);
	}

	@Override
	public final void close() {
		allocator.dealloc(new MemoryBlock(address, length));
	}

	public final void setByte(long index, byte value) {
		MemoryHelper.UNSAFE.putByte(address + index, value);
	}

	public final int getByte(long index) {
		return MemoryHelper.UNSAFE.getByte(address + index);
	}

	public final void setByteArray(long index, byte[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putByte(index, value[i]);
			index += Byte.BYTES;
		}
	}

	public final byte[] getByteArray(long index, int size) {
		byte[] array = new byte[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getByte(index);
			index += Byte.BYTES;
		}
		return array;
	}

	public final void setChar(long index, char value) {
		MemoryHelper.UNSAFE.putChar(address + index, value);
	}

	public final char getChar(long index) {
		return MemoryHelper.UNSAFE.getChar(address + index);
	}

	public final void setCharArray(long index, char[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putChar(index, value[i]);
			index += Character.BYTES;
		}
	}

	public final char[] getCharArray(long index, int size) {
		char[] array = new char[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getChar(index);
			index += Character.BYTES;
		}
		return array;
	}

	public final void setShort(long index, short value) {
		MemoryHelper.UNSAFE.putShort(address + index, value);
	}

	public final short getShort(long index) {
		return MemoryHelper.UNSAFE.getShort(address + index);
	}

	public final void setShortArray(long index, short[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putShort(index, value[i]);
			index += Short.BYTES;
		}
	}

	public final short[] getShortArray(long index, int size) {
		short[] array = new short[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getShort(index);
			index += Short.BYTES;
		}
		return array;
	}

	public final void setInt(long index, int value) {
		MemoryHelper.UNSAFE.putInt(address + index, value);
	}

	public final int getInt(long index) {
		return MemoryHelper.UNSAFE.getInt(address + index);
	}

	public final void setIntArray(long index, int[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putInt(index, value[i]);
			index += Integer.BYTES;
		}
	}

	public final int[] getIntArray(long index, int size) {
		int[] array = new int[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getInt(index);
			index += Integer.BYTES;
		}
		return array;
	}

	public final void setLong(long index, long value) {
		MemoryHelper.UNSAFE.putLong(address + index, value);
	}

	public final long getLong(long index) {
		return MemoryHelper.UNSAFE.getLong(address + index);
	}

	public final void setLongArray(long index, long[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putLong(index, value[i]);
			index += Long.BYTES;
		}
	}

	public final long[] getLongArray(long index, int size) {
		long[] array = new long[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getLong(index);
			index += Long.BYTES;
		}
		return array;
	}

	public final void setFloat(long index, float value) {
		MemoryHelper.UNSAFE.putFloat(address + index, value);
	}

	public float getFloat(long index) {
		return MemoryHelper.UNSAFE.getFloat(address + index);
	}

	public final void setFloatArray(long index, float[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putFloat(index, value[i]);
			index += Float.BYTES;
		}
	}

	public final float[] getFloatArray(long index, int size) {
		float[] array = new float[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getFloat(index);
			index += Float.BYTES;
		}
		return array;
	}

	public final void setDouble(long index, double value) {
		MemoryHelper.UNSAFE.putDouble(address + index, value);
	}

	public final double getDouble(long index) {
		return MemoryHelper.UNSAFE.getDouble(address + index);
	}

	public final void setDoubleArray(long index, double[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putDouble(index, value[i]);
			index += Double.BYTES;
		}
	}

	public final double[] getDoubleArray(long index, int size) {
		double[] array = new double[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getDouble(index);
			index += Double.BYTES;
		}
		return array;
	}
}