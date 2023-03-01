package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.FloatList;
import org.jgine.collection.list.arrayList.FloatArrayList;

public class UnorderedFloatArrayList extends FloatArrayList implements FloatList {

	public UnorderedFloatArrayList() {
		super();
	}

	public UnorderedFloatArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedFloatArrayList(Collection<? extends Float> c) {
		super(c);
	}

	public UnorderedFloatArrayList(float[] array) {
		super(array);
	}

	public UnorderedFloatArrayList(float[] array, int size) {
		super(array, size);
	}

	public UnorderedFloatArrayList(FloatArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, float element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Float> c) {
		Iterator<? extends Float> itr = c.iterator();
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
	public float removeFloat(int index) {
		float element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
