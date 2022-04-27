package org.jgine.system.data;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;
import org.jgine.misc.utils.memory.MemoryHelper;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class ByteBufferSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends SystemScene<T1, T2> implements AutoCloseable {

	private static final Unsafe unsafe = MemoryHelper.UNSAFE;

	private final Allocator allocator;
	private MemoryBlock block;
	private long address;
	private int size;

	public ByteBufferSystemScene(T1 system, Scene scene, Allocator allocator, int dataSize) {
		super(system, scene);
		this.allocator = allocator;
		int allocSize = dataSize * ListSystemScene.GROW_SIZE;
		block = allocator.alloc(allocSize);
		address = block.address;
		unsafe.setMemory(address, allocSize, (byte) 0);
	}

	@Override
	public final void close() {
		allocator.dealloc(block);
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		return object;
	}

	@Override
	@Nullable
	public T2 removeObject(T2 object) {
		return object;
	}

	@Override
	public Collection<T2> getObjects() {
		return null;
	}

	protected final void setByte(long index, byte value) {
		unsafe.putByte(address + index, value);
	}

	protected final byte getByte(long index) {
		return unsafe.getByte(address + index);
	}

	protected final void setByteArray(long index, byte[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putByte(index, value[i]);
			index += Byte.BYTES;
		}
	}

	protected final byte[] getByteArray(long index, int size) {
		byte[] array = new byte[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getByte(index);
			index += Byte.BYTES;
		}
		return array;
	}

	protected final void setChar(long index, char value) {
		unsafe.putChar(address + index, value);
	}

	protected final char getChar(long index) {
		return unsafe.getChar(address + index);
	}

	protected final void setCharArray(long index, char[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putChar(index, value[i]);
			index += Character.BYTES;
		}
	}

	protected final char[] getCharArray(long index, int size) {
		char[] array = new char[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getChar(index);
			index += Character.BYTES;
		}
		return array;
	}

	protected final void setShort(long index, short value) {
		unsafe.putShort(address + index, value);
	}

	protected final short getShort(long index) {
		return unsafe.getShort(address + index);
	}

	protected final void setShortArray(long index, short[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putShort(index, value[i]);
			index += Short.BYTES;
		}
	}

	protected final short[] getShortArray(long index, int size) {
		short[] array = new short[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getShort(index);
			index += Short.BYTES;
		}
		return array;
	}

	protected final void setInt(long index, int value) {
		unsafe.putInt(address + index, value);
	}

	protected final int getInt(long index) {
		return unsafe.getInt(address + index);
	}

	protected final void setIntArray(long index, int[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putInt(index, value[i]);
			index += Integer.BYTES;
		}
	}

	protected final int[] getIntArray(long index, int size) {
		int[] array = new int[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getInt(index);
			index += Integer.BYTES;
		}
		return array;
	}

	protected final void setLong(long index, long value) {
		unsafe.putLong(address + index, value);
	}

	protected final long getLong(long index) {
		return unsafe.getLong(address + index);
	}

	protected final void setLongArray(long index, long[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putLong(index, value[i]);
			index += Long.BYTES;
		}
	}

	protected final long[] getLongArray(long index, int size) {
		long[] array = new long[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getLong(index);
			index += Long.BYTES;
		}
		return array;
	}

	protected final void setFloat(long index, float value) {
		unsafe.putFloat(address + index, value);
	}

	protected float getFloat(long index) {
		return unsafe.getFloat(address + index);
	}

	protected final void setFloatArray(long index, float[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putFloat(index, value[i]);
			index += Float.BYTES;
		}
	}

	protected final float[] getFloatArray(long index, int size) {
		float[] array = new float[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getFloat(index);
			index += Float.BYTES;
		}
		return array;
	}

	protected final void setDouble(long index, double value) {
		unsafe.putDouble(address + index, value);
	}

	protected final double getDouble(long index) {
		return unsafe.getDouble(address + index);
	}

	protected final void setDoubleArray(long index, double[] value) {
		index = address + index;
		for (int i = 0; i < value.length; i++) {
			unsafe.putDouble(index, value[i]);
			index += Double.BYTES;
		}
	}

	protected final double[] getDoubleArray(long index, int size) {
		double[] array = new double[size];
		index = address + index;
		for (int i = 0; i < size; i++) {
			array[i] = unsafe.getDouble(index);
			index += Double.BYTES;
		}
		return array;
	}
}
