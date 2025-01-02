package org.jgine.collection.list;

import java.util.Arrays;
import java.util.Collection;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class IndexList<E> extends ObjectArrayList<E> {

	private static final long serialVersionUID = 7390756637205529901L;

	protected int[] indexes;

	public IndexList() {
		this(8);
	}

	public IndexList(int capacity) {
		super(capacity);
		indexes = new int[capacity];
	}

	public IndexList(int[] indexes, E[] values) {
		super(values);
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
			a[0] = element;
			size++;
			return null;
		}
		for (int i = 0; i < size; i++) {
			int savedIndex = indexes[i];
			if (savedIndex == index) {
				E oldValue = a[i];
				indexes[i] = index;
				a[i] = element;
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
				a[i] = a[i - 1];
			}
		}
		indexes[pos] = index;
		a[pos] = element;
		size++;
	}

	@Override
	public E remove(int index) {
		int i = indexOf(index);
		if (i != -1) {
			E element = a[i];
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
			System.arraycopy(a, i + 1, a, i, size - i);
		}
		indexes[size] = 0;
		a[size] = null;
	}

	@Override
	public E get(int index) {
		return a[indexOf(index)];
	}

	public E getIntern(int i) {
		return a[i];
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

	protected void resize(int size) {
		@SuppressWarnings("unchecked")
		E[] newArray = (E[]) new Object[size];
		System.arraycopy(a, 0, newArray, 0, this.size);
		a = newArray;

		int[] newArray2 = new int[size];
		System.arraycopy(indexes, 0, newArray2, 0, indexes.length);
		indexes = newArray2;
	}

	public void trimToSize() {
		if (size != a.length) {
			@SuppressWarnings("unchecked")
			E[] newArray = (E[]) new Object[size];
			System.arraycopy(a, 0, newArray, 0, size);
			a = newArray;
			int[] newArray2 = new int[size];
			System.arraycopy(indexes, 0, newArray2, 0, size);
			indexes = newArray2;
		}
	}
}
