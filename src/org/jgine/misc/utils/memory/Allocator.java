package org.jgine.misc.utils.memory;

import org.jgine.misc.utils.memory.allocator.DefaultAllocator;

/**
 * Represents the base Allocator class.
 * 
 * @author Maximilian Paar
 */
public abstract class Allocator implements AutoCloseable {

	public static final Allocator DEFAULT_ALLOCATOR = new DefaultAllocator();

	/**
	 * Deallocates all allocated memory.
	 */
	@Override
	public abstract void close();

	/**
	 * Allocates a block of memory with the given size.
	 * 
	 * @param size of the memory to be allocated in byte
	 * @return a block of memory
	 */
	public abstract MemoryBlock alloc(long size);

	/**
	 * Allocates all available memory in the allocator. This evidently does nothing
	 * on base allocators like the DefaultAllocator which are supposed to be a
	 * fallback to OS allocation.
	 * 
	 * @return a block of memory
	 */
	public abstract MemoryBlock allocAll();

	/**
	 * Deallocates the given memory block.
	 * 
	 * @param block the be deallocated
	 */
	public abstract void dealloc(MemoryBlock block);

	/**
	 * Deallocates all available memory in the allocator. This evidently does
	 * nothing on base allocators like the DefaultAllocator which are supposed to be
	 * a fallback to OS allocation.
	 */
	public abstract void deallocAll();

	/**
	 * Tries to expand the given memory block in the Allocator that owns it.
	 * 
	 * @param block to be expanded
	 * @param delta between the old and the new size
	 * @return boolean if operation was successful
	 */
	public abstract boolean expand(MemoryBlock block, long delta);

	/**
	 * Reallocates the given memory block with the given size.
	 * 
	 * @param block to be realicated
	 * @param size of the memory to be allocated in byte
	 */
	public abstract void realloc(MemoryBlock block, long size);

	/**
	 * Checks if the given memory block is owned by this allocator.
	 * 
	 * @param block to be checked
	 * @return boolean if owned
	 */
	public abstract boolean owns(MemoryBlock block);
}
