package org.jgine.collection.list;

import java.util.Arrays;
import java.util.Collection;

import it.unimi.dsi.fastutil.ints.IntCollection;

public class IndexList<E> extends FastArrayList<E> {

	protected static final int DEFAULT_CAPACITY = 10;

	protected int[] indexes;

	public IndexList() {
		this(DEFAULT_CAPACITY);
	}

	public IndexList(int capacity) {
		super(capacity);
		indexes = new int[capacity];
	}

	public IndexList(int[] indexes, E[] values) {
		super(values);
		this.indexes = indexes;
	}

	public IndexList(int[] indexes, E[] values, int size) {
		super(values, size);
		this.indexes = indexes;
	}

	@Override
	public boolean add(E element) {
		set(indexes[size - 1] + 1, element);
		return true;
	}

	@Override
	public void add(int index, E element) {
		set(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		if (size == 0) {
			indexes[0] = index;
			array[0] = element;
			size++;
			return null;
		}
		for (int i = 0; i < size; i++) {
			int savedIndex = indexes[i];
			if (savedIndex == index) {
				E oldValue = array[i];
				indexes[i] = index;
				array[i] = element;
				return oldValue;
			}
			if (index < savedIndex) {
				setIntern(i, index, element);
				return null;
			}
		}
		setIntern(size - 1, index, element);
		return null;
	}

	private void setIntern(int pos, int index, E element) {
		ensureCapacity(indexes.length + 1);
		if (index != size - 1) {
			for (int i = size - 2; i > index; i--) {
				indexes[i] = indexes[i - 1];
				array[i] = array[i - 1];
			}
		}
		indexes[pos] = index;
		array[pos] = element;
		size++;
	}

	@Override
	public E remove(int index) {
		int i = indexOf(index);
		if (i != -1) {
			E element = array[i];
			removeIntern(i);
			return element;
		}
		return null;
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i != -1) {
			removeIntern(i);
			return true;
		}
		return false;
	}

	public void removeIntern(int i) {
		if (i != --size) {
			System.arraycopy(indexes, i + 1, indexes, i, size - i);
			System.arraycopy(array, i + 1, array, i, size - i);
		}
		indexes[size] = 0;
		array[size] = null;
	}

	@Override
	public E get(int index) {
		return array[indexOf(index)];
	}

	public E getIntern(int i) {
		return array[i];
	}

	public int indexOf(int index) {
		for (int i = 0; i < size; i++)
			if (indexes[i] == index)
				return i;
		return -1;
	}

	public int lastIndexOf(int index) {
		for (int i = size; i >= 0; i--)
			if (indexes[i] == index)
				return i;
		return -1;
	}

	public boolean containsIndex(int index) {
		return indexOf(index) != -1;
	}

	public boolean containsAllIndex(IntCollection c) {
		for (int i : c)
			if (!containsIndex(i))
				return false;
		return true;
	}

	@Override
	public void clear() {
		if (size > 0) {
			Arrays.fill(indexes, 0, size, 0);
			super.clear();
		}
	}

	@Override
	public IndexList<E> clone() {
		IndexList<E> clone = (IndexList<E>) super.clone();
		clone.indexes = indexes.clone();
		return clone;
	}

	@Override
	protected void resize(int size) {
		super.resize(size);
		int[] newArray = new int[size];
		System.arraycopy(indexes, 0, newArray, 0, indexes.length);
		indexes = newArray;
	}

	@Override
	public void trimToSize() {
		if (size != array.length) {
			@SuppressWarnings("unchecked")
			E[] newArray = (E[]) new Object[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
			int[] newArray2 = new int[size];
			System.arraycopy(indexes, 0, newArray2, 0, size);
			indexes = newArray2;
		}
	}
}
