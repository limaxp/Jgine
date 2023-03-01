package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.DoubleList;
import org.jgine.collection.list.arrayList.DoubleArrayList;

public class UnorderedDoubleArrayList extends DoubleArrayList implements DoubleList {

	public UnorderedDoubleArrayList() {
		super();
	}

	public UnorderedDoubleArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedDoubleArrayList(Collection<? extends Double> c) {
		super(c);
	}

	public UnorderedDoubleArrayList(double[] array) {
		super(array);
	}

	public UnorderedDoubleArrayList(double[] array, int size) {
		super(array, size);
	}

	public UnorderedDoubleArrayList(DoubleArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, double element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Double> c) {
		Iterator<? extends Double> itr = c.iterator();
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
	public double removeDouble(int index) {
		double element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
