package org.jgine.misc.collection.list.offheap;

import org.jgine.misc.utils.memory.MemoryHelper;

public abstract class OffheapObject {

	private long address;

	public OffheapObject(int size) {
		address = MemoryHelper.allocateMemory(29);
	}

	public abstract void set(long address);

	public void writeAddress(long address) {
		set(address);
		this.address = address;
	}

	public void setAddress(long address) {
		this.address = address;
	}

	public long getAddress() {
		return address;
	}

	protected void setByte(int offset, byte value) {
		MemoryHelper.putByte(address + offset, value);
	}

	protected void setChar(int offset, char value) {
		MemoryHelper.putChar(address + offset, value);
	}

	protected void setShort(int offset, short value) {
		MemoryHelper.putShort(address + offset, value);
	}

	protected void setInt(int offset, int value) {
		MemoryHelper.putInt(address + offset, value);
	}

	protected void setLong(int offset, long value) {
		MemoryHelper.putLong(address + offset, value);
	}

	protected void setFloat(int offset, float value) {
		MemoryHelper.putFloat(address + offset, value);
	}

	protected void setDouble(int offset, double value) {
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

	protected static void setByte_(long address, byte value) {
		MemoryHelper.putByte(address, value);
	}

	protected static void setChar_(long address, char value) {
		MemoryHelper.putChar(address, value);
	}

	protected static void setShort_(long address, short value) {
		MemoryHelper.putShort(address, value);
	}

	protected static void setInt_(long address, int value) {
		MemoryHelper.putInt(address, value);
	}

	protected static void setLong_(long address, long value) {
		MemoryHelper.putLong(address, value);
	}

	protected static void setFloat_(long address, float value) {
		MemoryHelper.putFloat(address, value);
	}

	protected static void setDouble_(long address, double value) {
		MemoryHelper.putDouble(address, value);
	}

	protected static byte getByte_(long address) {
		return MemoryHelper.getByte(address);
	}

	protected static char getChar_(long address) {
		return MemoryHelper.getChar(address);
	}

	protected static short getShort_(long address) {
		return MemoryHelper.getShort(address);
	}

	protected static int getInt_(long address) {
		return MemoryHelper.getInt(address);
	}

	protected static long getLong_(long address) {
		return MemoryHelper.getLong(address);
	}

	protected static float getFloat_(long address) {
		return MemoryHelper.getFloat(address);
	}

	protected static double getDouble_(long address) {
		return MemoryHelper.getDouble(address);
	}
}
