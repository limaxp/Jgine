package org.jgine.misc.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.misc.collection.list.AbstractLongList;
import org.jgine.misc.collection.list.LongList;

/**
 * An Long ArrayList implementation that does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class LongArrayList extends AbstractLongList implements LongList {

	protected long[] array;
	protected int size;

	public LongArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public LongArrayList(int capacity) {
		array = new long[capacity];
		size = 0;
	}

	public LongArrayList(Collection<? extends Long> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public LongArrayList(long[] array) {
		this.array = array;
		size = array.length;
	}

	public LongArrayList(long[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public LongArrayList(LongArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(long element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(long element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, long element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Long> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Long> c) {
		Iterator<? extends Long> itr = c.iterator();
		int addSize = c.size();

		if (addSize + size > array.length)
			ensureCapacity(size + addSize);
		int lastIndex = index + addSize;
		if (size > 0 && index != size)
			System.arraycopy(array, index, array, lastIndex, size - index);
		for (; index < lastIndex; index++)
			array[index] = itr.next();
		size += addSize;
		return addSize > 0;
	}

	@Override
	public long set(int index, long element) {
		long prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(long element) {
		int index = indexOf(element);
		if (index != -1) {
			removeLong(index);
			return true;
		}
		return false;
	}

	@Override
	public long removeLong(int index) {
		long element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((long) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public long getLong(int index) {
		return array[index];
	}

	@Override
	public int indexOf(long element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(long element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(long element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((long) o))
				return false;
		return true;
	}

	@Override
	public void clear() {
		this.size = 0;
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
	public LongArrayList clone() {
		return new LongArrayList(this);
	}

	@Override
	public Long[] toArray() {
		Long[] arr = new Long[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public long[] toLongArray() {
		return Arrays.copyOf(array, size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		else if (a.length > size)
			a[size] = null;
		System.arraycopy(array, 0, a, 0, size);
		return a;
	};

	@Override
	public long[] toLongArray(long[] a) {
		if (a.length < size)
			a = new long[size];
		else if (a.length > size)
			a[size] = 0;
		System.arraycopy(array, 0, a, 0, size);
		return a;
	}

	protected void ensureCapacity(int minCapacity) {
		int length = array.length;
		if (minCapacity > length)
			resize(Math.max(length * 2, minCapacity));
	}

	protected void resize(int size) {
		long[] newArray = new long[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			long[] newArray = new long[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
