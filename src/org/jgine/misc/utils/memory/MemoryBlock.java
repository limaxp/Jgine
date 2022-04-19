package org.jgine.misc.utils.memory;

/**
 * Represents a block of memory with address and size.
 * 
 * @author Maximilian Paar
 */
public class MemoryBlock {

	public long address;
	public long size;

	public MemoryBlock() {}

	public MemoryBlock(long address, long size) {
		this.address = address;
		this.size = size;
	}
}
