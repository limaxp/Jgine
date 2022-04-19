package org.jgine.misc.utils.memory.allocator;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;

public class StackAllocator extends Allocator {

	private Allocator parent;
	private long startAddress;
	private long currentAddress;
	private long size;

	public StackAllocator(long size) {
		this(Allocator.DEFAULT_ALLOCATOR, size);
	}

	public StackAllocator(Allocator parent, long size) {
		this.parent = parent;
		this.size = size;
		startAddress = parent.alloc(size).address;
		currentAddress = startAddress;
	}

	@Override
	public void close() {
		parent.dealloc(new MemoryBlock(startAddress, size));
	}

	@Override
	public MemoryBlock alloc(long size) {
		long n1 = roundToAligned(size);
		MemoryBlock block = new MemoryBlock();
		if (n1 > (startAddress + size) - currentAddress) {
			block.address = 0;
			block.size = 0;
			return block;
		}
		block.address = currentAddress;
		block.size = size;
		currentAddress += n1;
		return block;
	}

	@Override
	public MemoryBlock allocAll() {
		long lastAddress = startAddress + size;
		long allocSize = lastAddress - currentAddress;
		MemoryBlock block = new MemoryBlock();
		if (allocSize > 0) {
			block.address = currentAddress;
			block.size = allocSize;
			currentAddress = lastAddress;
			return block;
		}
		block.address = 0;
		block.size = 0;
		return block;
	}

	@Override
	public void dealloc(MemoryBlock block) {
		if (block.address + roundToAligned(block.size) == currentAddress)
			currentAddress = block.address;
	}

	public void deallocAll() {
		currentAddress = startAddress;
	}

	@Override
	public boolean expand(MemoryBlock block, long delta) {
		if (block.address + roundToAligned(block.size) == currentAddress && delta <= (startAddress + size)
				- currentAddress) {
			block.size = block.size + delta;
			currentAddress += delta;
			return true;
		}
		return false;
	}

	@Override
	public void realloc(MemoryBlock block, long size) {
		if (block.address + roundToAligned(block.size) == currentAddress && size <= (startAddress + this.size)
				- currentAddress) {
			long delta = size - block.size;
			if (delta < 0)
				delta *= -1;
			currentAddress += delta;
			block.size = size;
		}
	}

	@Override
	public boolean owns(MemoryBlock block) {
		return (block.address >= startAddress && block.address < startAddress + size) || parent.owns(block);
	}

	private long roundToAligned(long l) {
		// TODO Auto-generated method stub
		return 0;
	}

}
