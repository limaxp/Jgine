package org.jgine.misc.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.misc.collection.list.CharList;
import org.jgine.misc.collection.list.arrayList.CharArrayList;

public class UnorderedCharArrayList extends CharArrayList implements CharList {

	public UnorderedCharArrayList() {
		super();
	}

	public UnorderedCharArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedCharArrayList(Collection<? extends Character> c) {
		super(c);
	}

	public UnorderedCharArrayList(char[] array) {
		super(array);
	}

	public UnorderedCharArrayList(char[] array, int size) {
		super(array, size);
	}

	public UnorderedCharArrayList(CharArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, char element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Character> c) {
		Iterator<? extends Character> itr = c.iterator();
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
	public char removeChar(int index) {
		char element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
