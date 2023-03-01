package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.LongList;
import org.jgine.collection.list.arrayList.LongArrayList;

public class UnorderedLongArrayList extends LongArrayList implements LongList {

	public UnorderedLongArrayList() {
		super();
	}

	public UnorderedLongArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedLongArrayList(Collection<? extends Long> c) {
		super(c);
	}

	public UnorderedLongArrayList(long[] array) {
		super(array);
	}

	public UnorderedLongArrayList(long[] array, int size) {
		super(array, size);
	}

	public UnorderedLongArrayList(LongArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, long element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Long> c) {
		Iterator<? extends Long> itr = c.iterator();
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
	public long removeLong(int index) {
		long element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
