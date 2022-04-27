package org.jgine.misc.collection.list.offheap;

import java.nio.ByteBuffer;

import org.jgine.misc.utils.memory.MemoryHelper;
import org.jgine.misc.utils.memory.Pointer;
import org.jgine.misc.utils.reflection.Reflection;
import org.lwjgl.system.MemoryUtil;

public class OffheapList<E> implements AutoCloseable {

	protected static final int DEFAULT_CAPACITY = 10;

	protected int objectSize;
	protected ByteBuffer buffer;
	protected int bufferSize;
	protected int size;

	public OffheapList(Class<E> clazz) {
		this(clazz, DEFAULT_CAPACITY);
	}

	public OffheapList(Class<E> clazz, int capacity) {
		objectSize = MemoryHelper.sizeOf(Reflection.newInstance(clazz));
		bufferSize = capacity;
		buffer = MemoryUtil.memAlloc(objectSize * bufferSize);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	public long add(E element) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		long address = MemoryUtil.memAddress(buffer) + (size++ * objectSize);
		MemoryHelper.copyMemory(element, address, objectSize);
		return address;
	}

	public long set(int index, E element) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		MemoryHelper.copyMemory(element, address, objectSize);
		return address;
	}

	public long remove(int index) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		MemoryUtil.memSet(address, 0, objectSize);
		// TODO rearange list
		return address;
	}

	public E get(int index) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		return new Pointer<E>().address(address).data;
	}

	public void clear() {
		size = 0;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public OffheapList<E> clone() {
		try {
			@SuppressWarnings("unchecked")
			OffheapList<E> clone = (OffheapList<E>) super.clone();
			clone.objectSize = objectSize;
			clone.buffer = MemoryUtil.memAlloc(objectSize * bufferSize);
			clone.buffer.put(0, buffer, 0, objectSize * size);
			clone.bufferSize = bufferSize;
			clone.size = size;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void ensureCapacity(int minCapacity) {
		if (minCapacity > bufferSize)
			resize(Math.max(bufferSize * 2, minCapacity));
	}

	protected void resize(int size) {
		ByteBuffer newBuffer = MemoryUtil.memAlloc(objectSize * size);
		buffer = newBuffer.put(0, buffer, 0, objectSize * bufferSize);
		bufferSize = size;
	}

	public void trimToSize() {
		if (size != bufferSize) {
			ByteBuffer newBuffer = MemoryUtil.memAlloc(objectSize * size);
			buffer = newBuffer.put(0, buffer, 0, objectSize * size);
			bufferSize = size;
		}
	}
}
