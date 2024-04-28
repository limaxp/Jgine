package org.jgine.collection.list.offheap;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import maxLibs.utils.Reflection;

public class OffheapList<E extends OffheapObject> implements AutoCloseable {

	protected static final int DEFAULT_CAPACITY = 10;

	protected Class<E> clazz;
	protected int objectSize;
	protected ByteBuffer buffer;
	protected int bufferSize;
	protected int size;
	protected E[] pointer;

	public OffheapList(Class<E> clazz) {
		this(clazz, DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public OffheapList(Class<E> clazz, int capacity) {
		this.clazz = clazz;
		this.objectSize = Reflection.sizeOf(clazz);
		bufferSize = capacity;
		buffer = MemoryUtil.memAlloc(objectSize * bufferSize);
		pointer = (E[]) Array.newInstance(clazz, bufferSize);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	public int add(E element) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		set(size, element);
		return size++;
	}

	public E remove(int index) {
		E element = pointer[index];
		if (element == null)
			return null;

		set(index, pointer[--size]);
		pointer[size] = null;
		return element;
	}

	protected void set(int index, E element) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		element.write(address);
		element.setAddress(address);
		pointer[index] = element;
	}

	public E get(int index) {
		E element = pointer[index];
		if (element == null) {
			long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
			element = Reflection.newInstance(clazz);
			element.setAddress(address);
			pointer[index] = element;
		}
		return element;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		size = 0;
		// TODO change!
		pointer = (E[]) Array.newInstance(clazz, bufferSize);
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
			clone.pointer = (E[]) pointer.clone();
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
		newBuffer.put(0, buffer, 0, objectSize * bufferSize);
		MemoryUtil.memFree(buffer);
		buffer = newBuffer;
		bufferSize = size;
		@SuppressWarnings("unchecked")
		E[] newArray = (E[]) Array.newInstance(clazz, size);
		System.arraycopy(pointer, 0, newArray, 0, this.size);
		pointer = newArray;
	}

	public void trimToSize() {
		if (size != bufferSize) {
			ByteBuffer newBuffer = MemoryUtil.memAlloc(objectSize * size);
			buffer = newBuffer.put(0, buffer, 0, objectSize * size);
			bufferSize = size;
			@SuppressWarnings("unchecked")
			E[] newArray = (E[]) Array.newInstance(clazz, size);
			System.arraycopy(pointer, 0, newArray, 0, size);
			pointer = newArray;
		}
	}
}
