package org.jgine.utils.collection.list.offheap;

import org.lwjgl.system.MemoryUtil;

public abstract class OffheapObject {

	public abstract void setAddress(long address);

	public abstract long getAddress();

	public abstract void write(long address);

	protected void setByte(int offset, byte value) {
		MemoryUtil.memPutByte(getAddress() + offset, value);
	}

	protected void setShort(int offset, short value) {
		MemoryUtil.memPutShort(getAddress() + offset, value);
	}

	protected void setInt(int offset, int value) {
		MemoryUtil.memPutInt(getAddress() + offset, value);
	}

	protected void setLong(int offset, long value) {
		MemoryUtil.memPutLong(getAddress() + offset, value);
	}

	protected void setFloat(int offset, float value) {
		MemoryUtil.memPutFloat(getAddress() + offset, value);
	}

	protected void setDouble(int offset, double value) {
		MemoryUtil.memPutDouble(getAddress() + offset, value);
	}

	protected byte getByte(int offset) {
		return MemoryUtil.memGetByte(getAddress() + offset);
	}

	protected short getShort(int offset) {
		return MemoryUtil.memGetShort(getAddress() + offset);
	}

	protected int getInt(int offset) {
		return MemoryUtil.memGetInt(getAddress() + offset);
	}

	protected long getLong(int offset) {
		return MemoryUtil.memGetLong(getAddress() + offset);
	}

	protected float getFloat(int offset) {
		return MemoryUtil.memGetFloat(getAddress() + offset);
	}

	protected double getDouble(int offset) {
		return MemoryUtil.memGetDouble(getAddress() + offset);
	}

	protected static void setByte_(long address, byte value) {
		MemoryUtil.memPutByte(address, value);
	}

	protected static void setShort_(long address, short value) {
		MemoryUtil.memPutShort(address, value);
	}

	protected static void setInt_(long address, int value) {
		MemoryUtil.memPutInt(address, value);
	}

	protected static void setLong_(long address, long value) {
		MemoryUtil.memPutLong(address, value);
	}

	protected static void setFloat_(long address, float value) {
		MemoryUtil.memPutFloat(address, value);
	}

	protected static void setDouble_(long address, double value) {
		MemoryUtil.memPutDouble(address, value);
	}

	protected static byte getByte_(long address) {
		return MemoryUtil.memGetByte(address);
	}

	protected static short getShort_(long address) {
		return MemoryUtil.memGetShort(address);
	}

	protected static int getInt_(long address) {
		return MemoryUtil.memGetInt(address);
	}

	protected static long getLong_(long address) {
		return MemoryUtil.memGetLong(address);
	}

	protected static float getFloat_(long address) {
		return MemoryUtil.memGetFloat(address);
	}

	protected static double getDouble_(long address) {
		return MemoryUtil.memGetDouble(address);
	}
}
