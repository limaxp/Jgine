package org.jgine.misc.utils.memory.allocator;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;

public class SegregatorAllocator<T1 extends Allocator, T2 extends Allocator> extends Allocator {

	private int threshold;
	private T1 smallAllocator;
	private T2 bigAllocator;

	public SegregatorAllocator(int threshold, T1 smallAllocator, T2 bigAllocator) {
		this.threshold = threshold;
		this.smallAllocator = smallAllocator;
		this.bigAllocator = bigAllocator;
	}

	@Override
	public void close() {
		smallAllocator.close();
		bigAllocator.close();
	}

	@Override
	public MemoryBlock alloc(long size) {
		if (size < threshold)
			return smallAllocator.alloc(size);
		return bigAllocator.alloc(size);
	}

	@Override
	public MemoryBlock allocAll() {
		MemoryBlock block = smallAllocator.allocAll();
		if (block.address == 0)
			block = bigAllocator.allocAll();
		return block;
	}

	@Override
	public void dealloc(MemoryBlock block) {
		if (block.size < threshold)
			smallAllocator.dealloc(block);
		bigAllocator.dealloc(block);
	}

	@Override
	public void deallocAll() {
		smallAllocator.deallocAll();
		bigAllocator.deallocAll();
	}

	@Override
	public boolean expand(MemoryBlock block, long delta) {
		if (smallAllocator.owns(block))
			return smallAllocator.expand(block, delta);
		return bigAllocator.expand(block, delta);
	}

	@Override
	public void realloc(MemoryBlock block, long size) {
		boolean underThreshold = size < threshold;
		if (smallAllocator.owns(block)) {
			if (underThreshold)
				smallAllocator.realloc(block, size);
			else {
				smallAllocator.dealloc(block);
				MemoryBlock newBlock = bigAllocator.alloc(size);
				block.address = newBlock.address;
				block.size = size;
			}
		}
		else {
			if (!underThreshold)
				bigAllocator.realloc(block, size);
			else {
				bigAllocator.dealloc(block);
				MemoryBlock newBlock = smallAllocator.alloc(size);
				block.address = newBlock.address;
				block.size = size;
			}
		}
	}

	@Override
	public boolean owns(MemoryBlock block) {
		return smallAllocator.owns(block) || bigAllocator.owns(block);
	}
}
