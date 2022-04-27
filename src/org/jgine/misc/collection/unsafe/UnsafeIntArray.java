package org.jgine.misc.collection.unsafe;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;
import org.jgine.misc.utils.memory.MemoryHelper;

@SuppressWarnings("restriction")
public class UnsafeIntArray implements AutoCloseable {

	private final Allocator allocator;
	public final long address;
	public final long length;

	public UnsafeIntArray(long size) {
		this(Allocator.DEFAULT_ALLOCATOR, size);
	}

	public UnsafeIntArray(Allocator allocator, long size) {
		this.allocator = allocator;
		this.length = size;
		long allocSize = size * Integer.BYTES;
		address = allocator.alloc(allocSize).address;
		MemoryHelper.UNSAFE.setMemory(address, allocSize, (byte) 0);
	}

	@Override
	public final void close() {
		allocator.dealloc(new MemoryBlock(address, length * Integer.BYTES));
	}

	public final void setInt(long index, int value) {
		MemoryHelper.UNSAFE.putInt(address + (index * Integer.BYTES), value);
	}

	public final int getInt(long index) {
		return MemoryHelper.UNSAFE.getInt(address + (index * Integer.BYTES));
	}

	public final void setIntArray(long index, int[] value) {
		index = address + (index * Integer.BYTES);
		for (int i = 0; i < value.length; i++) {
			MemoryHelper.UNSAFE.putInt(index, value[i]);
			index += Integer.BYTES;
		}
	}

	public final int[] getIntArray(long index, int size) {
		int[] array = new int[size];
		index = address + (index * Integer.BYTES);
		for (int i = 0; i < size; i++) {
			array[i] = MemoryHelper.UNSAFE.getInt(index);
			index += Integer.BYTES;
		}
		return array;
	}
}
