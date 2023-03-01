package org.jgine.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.List;

/**
 * An ArrayList implementation that does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class FastArrayList<E> extends AbstractList<E> implements List<E> {

	protected static final int DEFAULT_CAPACITY = 10;

	protected E[] array;
	protected int size;

	public FastArrayList() {
		this(DEFAULT_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public FastArrayList(int capacity) {
		array = (E[]) new Object[capacity];
		size = 0;
	}

	public FastArrayList(Collection<? extends E> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public FastArrayList(E[] array) {
		this.array = array;
		size = array.length;
	}

	public FastArrayList(E[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public void setArray(E[] array) {
		this.array = array;
		this.size = array.length;
	}

	public void setArray(E[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public E[] getArray() {
		return array;
	}

	@Override
	public boolean add(E element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(E element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, E element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Iterator<? extends E> itr = c.iterator();
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
	public E set(int index, E element) {
		E prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public E remove(int index) {
		E element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = null;
		return element;
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index != -1) {
			remove(index);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (remove(o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public E get(int index) {
		return array[index];
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0; i < size; i++)
			if (array[i].equals(o))
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size; i >= 0; i--)
			if (array[i].equals(o))
				return i;
		return -1;
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public void clear() {
		if (size > 0) {
			Arrays.fill(array, 0, size, null);
			this.size = 0;
		}
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
	public FastArrayList<E> clone() {
		try {
			@SuppressWarnings("unchecked")
			FastArrayList<E> clone = (FastArrayList<E>) super.clone();
			clone.array = (E[]) array.clone();
			clone.size = size;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(array, size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		else if (a.length > size)
			a[size] = null;
		System.arraycopy(array, 0, a, 0, size);
		return a;
	};

	protected void ensureCapacity(int minCapacity) {
		int length = array.length;
		if (minCapacity > length)
			resize(Math.max(length * 2, minCapacity));
	}

	protected void resize(int size) {
		@SuppressWarnings("unchecked")
		E[] newArray = (E[]) new Object[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			@SuppressWarnings("unchecked")
			E[] newArray = (E[]) new Object[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
