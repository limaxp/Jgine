package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.ShortList;
import org.jgine.collection.list.arrayList.ShortArrayList;

public class UnorderedShortArrayList extends ShortArrayList implements ShortList {

	public UnorderedShortArrayList() {
		super();
	}

	public UnorderedShortArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedShortArrayList(Collection<? extends Short> c) {
		super(c);
	}

	public UnorderedShortArrayList(short[] array) {
		super(array);
	}

	public UnorderedShortArrayList(short[] array, int size) {
		super(array, size);
	}

	public UnorderedShortArrayList(ShortArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, short element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Short> c) {
		Iterator<? extends Short> itr = c.iterator();
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
	public short removeShort(int index) {
		short element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
