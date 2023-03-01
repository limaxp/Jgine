package org.jgine.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.ByteList;
import org.jgine.collection.list.arrayList.ByteArrayList;

public class UnorderedByteArrayList extends ByteArrayList implements ByteList {

	public UnorderedByteArrayList() {
		super();
	}

	public UnorderedByteArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedByteArrayList(Collection<? extends Byte> c) {
		super(c);
	}

	public UnorderedByteArrayList(byte[] array) {
		super(array);
	}

	public UnorderedByteArrayList(byte[] array, int size) {
		super(array, size);
	}

	public UnorderedByteArrayList(ByteArrayList orig) {
		super(orig);
	}

	@Override
	public void add(int index, byte element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			array[size] = array[index];
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Byte> c) {
		Iterator<? extends Byte> itr = c.iterator();
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
	public byte removeByte(int index) {
		byte element = array[index];
		if (index != --size)
			array[index] = array[size];
		array[size] = 0;
		return element;
	}
}
