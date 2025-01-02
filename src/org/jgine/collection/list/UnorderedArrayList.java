package org.jgine.collection.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * An ArrayList implementation. Insert order is NOT persistent.
 */
public class UnorderedArrayList<E> extends ObjectArrayList<E> implements List<E> {

	private static final long serialVersionUID = -7215362169841723497L;

	public UnorderedArrayList() {
		super(8);
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

	@Override
	public void add(int index, E element) {
		if (size == a.length)
			ensureCapacity(size + 1);
		if (index != size)
			a[size] = a[index];
		a[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Iterator<? extends E> itr = c.iterator();
		int addSize = c.size();

		if (addSize + size > a.length)
			ensureCapacity(size + addSize);
		int lastIndex = index + addSize;
		if (size > 0 && index != size)
			System.arraycopy(a, index, a, size, addSize);
		for (; index < lastIndex; index++)
			a[index] = itr.next();
		size += addSize;
		return addSize > 0;
	}

	@Override
	public E remove(int index) {
		E element = a[index];
		if (index != --size)
			a[index] = a[size];
		a[size] = null;
		return element;
	}
}
