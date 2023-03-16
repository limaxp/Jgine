package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jgine.collection.list.arrayList.FastArrayList;

/**
 * An ArrayList implementation that does NOT do range checks. Insert order is
 * NOT persistent.
 */
public class UnorderedArrayList<E> extends FastArrayList<E> implements List<E> {

	public UnorderedArrayList() {
		super();
	}

	public UnorderedArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedArrayList(Collection<? extends E> c) {
		super(c);
	}

	public UnorderedArrayList(E[] array) {
		super(array);
	}

	public UnorderedArrayList(E[] array, int size) {
		super(array, size);
	}

	@Override
	public void add(int index, E element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Iterator<? extends E> itr = c.iterator();
		int addSize = c.size();

		if (addSize + size > array.length)
			ensureCapacity(size + addSize);
		int lastIndex = index + addSize;
		if (size > 0 && index != size)
			System.arraycopy(array, index, array, size, addSize);
		for (; index < lastIndex; index++)
			array[index] = itr.next();
		size += addSize;
		return addSize > 0;
	}

	@Override
	public E remove(int index) {
		E element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = null;
		return element;
	}
}
