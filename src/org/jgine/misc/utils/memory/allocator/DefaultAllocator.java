package org.jgine.misc.utils.memory.allocator;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;
import org.jgine.misc.utils.memory.MemoryHelper;

@SuppressWarnings("restriction")
public class DefaultAllocator extends Allocator {

	// public static final int alignment = platformAlligment;

	@Override
	public void close() {
	}

	@Override
	public MemoryBlock alloc(long size) {
		return new MemoryBlock(MemoryHelper.UNSAFE.allocateMemory(size), size);
	}

	@Override
	public MemoryBlock allocAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dealloc(MemoryBlock block) {
		MemoryHelper.UNSAFE.freeMemory(block.address);
	}

	@Override
	public void deallocAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean expand(MemoryBlock block, long delta) {
		long newSize = block.size + delta;
		block.address = MemoryHelper.UNSAFE.reallocateMemory(block.address, newSize);
		block.size = newSize;
		return true;
	}

	@Override
	public void realloc(MemoryBlock block, long size) {
		if (size < 1) {
			dealloc(block);
			block.address = 0;
			block.size = 0;
			return;
		}
		block.address = MemoryHelper.UNSAFE.reallocateMemory(block.address, size);
		block.size = size;
	}

	@Override
	public boolean owns(MemoryBlock address) {
		return false;
	}
}
