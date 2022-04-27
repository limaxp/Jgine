package org.jgine.misc.collection.list.offheap;

import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.Collection;

import org.jgine.misc.collection.list.List;
import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.utils.memory.MemoryHelper;
import org.jgine.misc.utils.reflection.Reflection;
import org.lwjgl.system.MemoryUtil;

public class OffheapList<E> extends AbstractList<E> implements List<E>, AutoCloseable {

	protected static final int DEFAULT_CAPACITY = 10;

	public final int objectSize;
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
		MemoryUtil.memSet(buffer, 0);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public boolean add(E element) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		setFast(size, element);
		size++;
		return true;
	}

	@Override
	public int insert(E element) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		setFast(size, element);
		return size++;
	}

	@Override
	public void add(int index, E element) {
		// TODO
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO
		return false;
	}

	@Override
	public E set(int index, E element) {
		setFast(index, element);
		// TODO build element to return!
		return null;
	}

	public void setFast(int index, E element) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		MemoryHelper.copyMemory(element, address, objectSize);
	}

	@Override
	public E remove(int index) {
		// TODO
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO
		return true;
	}

	@Override
	public E get(int index) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		return MemoryUtil.memGlobalRefToObject(address);
	}

	@Override
	public int indexOf(Object o) {
		// TODO
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO
		return -1;
	}

	@Override
	public boolean contains(Object o) {
		// TODO
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO
		return true;
	}

	@Override
	public void clear() {
		// TODO
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public FastArrayList<E> clone() {
		// TODO
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T[] toArray(T[] a) {
		// TODO
		return null;
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
