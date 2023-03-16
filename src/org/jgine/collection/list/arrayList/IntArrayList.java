package org.jgine.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.AbstractIntList;
import org.jgine.collection.list.IntList;

/**
 * An Integer ArrayList implementation that does NOT do range checks.
 */
public class IntArrayList extends AbstractIntList implements IntList {

	protected int[] array;
	protected int size;

	public IntArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public IntArrayList(int capacity) {
		array = new int[capacity];
		size = 0;
	}

	public IntArrayList(Collection<? extends Integer> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public IntArrayList(int[] array) {
		this.array = array;
		size = array.length;
	}

	public IntArrayList(int[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public IntArrayList(IntArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(int element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(int element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, int element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		Iterator<? extends Integer> itr = c.iterator();
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
	public int set(int index, int element) {
		int prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(int element) {
		int index = indexOf(element);
		if (index != -1) {
			removeInt(index);
			return true;
		}
		return false;
	}

	@Override
	public int removeInt(int index) {
		int element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((int) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public int getInt(int index) {
		return array[index];
	}

	@Override
	public int indexOf(int element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(int element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(int element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((int) o))
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
	public IntArrayList clone() {
		return new IntArrayList(this);
	}

	@Override
	public Integer[] toArray() {
		Integer[] arr = new Integer[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public int[] toIntArray() {
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
	public int[] toIntArray(int[] a) {
		if (a.length < size)
			a = new int[size];
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
		int[] newArray = new int[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			int[] newArray = new int[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
