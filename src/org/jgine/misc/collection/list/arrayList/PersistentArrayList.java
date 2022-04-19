package org.jgine.misc.collection.list.arrayList;

import java.util.Collection;
import java.util.List;

/**
 * An ArrayList implementation that does ensure that element indexes do NOT
 * change! This list can contain null values. So traversing it will require null
 * checks! Does NOT allow adding and setting! Use insert() method instead! Also
 * does NOT do range checks!
 * 
 * @author Maximilian Paar
 */
public class PersistentArrayList<E> extends FastArrayList<E> implements List<E> {

	protected int[] freeIndexes;
	protected int freeIndexSize;

	public PersistentArrayList() {
		this(DEFAULT_CAPACITY);
	}

	public PersistentArrayList(int capacity) {
		super(capacity);
		freeIndexes = new int[capacity];
		freeIndexSize = 0;
	}

	public PersistentArrayList(E[] array) {
		super(array);
		freeIndexes = new int[size];
		freeIndexSize = 0;
	}

	@Override
	public boolean add(E element) {
		return insert(element) != 0;
	}

	public int insert(E element) {
		int id;
		if (freeIndexSize > 0)
			id = freeIndexes[--freeIndexSize];
		else {
			id = size;
			if (id == array.length)
				ensureCapacity(id + 1);
			size++;
		}
		array[id] = element;
		return id;
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		E element = array[index];
		if (element != null) {
			if (freeIndexSize++ == freeIndexes.length)
				ensureFreeIndexCapacity(freeIndexSize);
			freeIndexes[freeIndexSize] = index;
			array[index] = null;
		}
		return element;
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		super.clear();
		this.freeIndexSize = 0;
	}

	@Override
	public int size() {
		return size - freeIndexSize;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public PersistentArrayList<E> clone() {
		PersistentArrayList<E> clone = (PersistentArrayList<E>) super.clone();
		if (clone != null) {
			clone.freeIndexes = freeIndexes.clone();
			clone.freeIndexSize = freeIndexSize;
		}
		return clone;
	}

	protected void ensureFreeIndexCapacity(int minCapacity) {
		int length = freeIndexes.length;
		if (minCapacity > length)
			resizeFreeIndexes(Math.max(length * 2, minCapacity));
	}

	protected void resizeFreeIndexes(int size) {
		int[] newFreeIndexes = new int[size];
		System.arraycopy(freeIndexes, 0, newFreeIndexes, 0, this.freeIndexSize);
		freeIndexes = newFreeIndexes;
	}

	public void trimToSize() {
		if (size != array.length) {
			@SuppressWarnings("unchecked")
			E[] newArray = (E[]) new Object[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
		if (freeIndexSize != freeIndexes.length) {
			int[] newFreeIndexes = new int[freeIndexSize];
			System.arraycopy(freeIndexes, 0, newFreeIndexes, 0, freeIndexSize);
			freeIndexes = newFreeIndexes;
		}
	}
}
