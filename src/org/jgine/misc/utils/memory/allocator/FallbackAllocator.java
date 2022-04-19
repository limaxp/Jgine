package org.jgine.misc.utils.memory.allocator;

import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryBlock;

public class FallbackAllocator<T1 extends Allocator, T2 extends Allocator> extends Allocator {

	private T1 primary;
	private T2 fallback;

	public FallbackAllocator(T1 primary, T2 fallback) {
		this.primary = primary;
		this.fallback = fallback;
	}

	@Override
	public void close() {
		primary.close();
		fallback.close();
		primary = null;
		fallback = null;
	}

	@Override
	public MemoryBlock alloc(long size) {
		MemoryBlock block = primary.alloc(size);
		if (block.address == 0)
			block = fallback.alloc(size);
		return block;
	}

	@Override
	public MemoryBlock allocAll() {
		MemoryBlock block = primary.allocAll();
		if (block.address == 0)
			block = fallback.allocAll();
		return block;
	}

	@Override
	public void dealloc(MemoryBlock block) {
		if (primary.owns(block))
			primary.dealloc(block);
		else
			fallback.dealloc(block);
	}

	@Override
	public void deallocAll() {
		primary.deallocAll();
		fallback.deallocAll();
	}

	@Override
	public boolean expand(MemoryBlock block, long delta) {
		if (primary.owns(block))
			return primary.expand(block, delta);
		return fallback.expand(block, delta);
	}

	@Override
	public void realloc(MemoryBlock block, long size) {
		if (primary.owns(block)) {
			primary.realloc(block, size);
			if (block.size != size) {
				primary.dealloc(block);
				MemoryBlock newBlock = fallback.alloc(size);
				block.address = newBlock.address;
				block.size = size;
			}
		}
		else
			fallback.realloc(block, size);
	}

	@Override
	public boolean owns(MemoryBlock block) {
		return primary.owns(block) || fallback.owns(block);
	}
}
