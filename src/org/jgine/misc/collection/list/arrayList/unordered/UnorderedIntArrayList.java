package org.jgine.misc.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.misc.collection.list.IntList;
import org.jgine.misc.collection.list.arrayList.IntArrayList;

public class UnorderedIntArrayList extends IntArrayList implements IntList {

	public UnorderedIntArrayList() {
		super();
	}

	public UnorderedIntArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedIntArrayList(Collection<? extends Integer> c) {
		super(c);
	}

	public UnorderedIntArrayList(int[] array) {
		super(array);
	}

	public UnorderedIntArrayList(int[] array, int size) {
		super(array, size);
	}

	public UnorderedIntArrayList(IntArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, int element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		Iterator<? extends Integer> itr = c.iterator();
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
	public int removeInt(int index) {
		int element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
