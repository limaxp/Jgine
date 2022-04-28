package org.jgine.misc.collection.list.offheap;

import java.nio.ByteBuffer;

import org.jgine.misc.utils.memory.MemoryHelper;

public abstract class OffheapObject {

	long address;

	public long getAddress() {
		return address;
	}

	public abstract void save(ByteBuffer buffer);

	protected void putByte(int offset, byte value) {
		MemoryHelper.putByte(address + offset, value);
	}

	protected void putChar(int offset, char value) {
		MemoryHelper.putChar(address + offset, value);
	}

	protected void putShort(int offset, short value) {
		MemoryHelper.putShort(address + offset, value);
	}

	protected void putInt(int offset, int value) {
		MemoryHelper.putInt(address + offset, value);
	}

	protected void putLong(int offset, long value) {
		MemoryHelper.putLong(address + offset, value);
	}

	protected void putFloat(int offset, float value) {
		MemoryHelper.putFloat(address + offset, value);
	}

	protected void putDouble(int offset, double value) {
		MemoryHelper.putDouble(address + offset, value);
	}

	protected byte getByte(int offset) {
		return MemoryHelper.getByte(address + offset);
	}

	protected char getChar(int offset) {
		return MemoryHelper.getChar(address + offset);
	}

	protected short getShort(int offset) {
		return MemoryHelper.getShort(address + offset);
	}

	protected int getInt(int offset) {
		return MemoryHelper.getInt(address + offset);
	}

	protected long getLong(int offset) {
		return MemoryHelper.getLong(address + offset);
	}

	protected float getFloat(int offset) {
		return MemoryHelper.getFloat(address + offset);
	}

	protected double getDouble(int offset) {
		return MemoryHelper.getDouble(address + offset);
	}
}
