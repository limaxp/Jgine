package org.jgine.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.AbstractShortList;
import org.jgine.collection.list.ShortList;

/**
 * An Short ArrayList implementation that does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class ShortArrayList extends AbstractShortList implements ShortList {

	protected short[] array;
	protected int size;

	public ShortArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public ShortArrayList(int capacity) {
		array = new short[capacity];
		size = 0;
	}

	public ShortArrayList(Collection<? extends Short> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public ShortArrayList(short[] array) {
		this.array = array;
		size = array.length;
	}

	public ShortArrayList(short[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public ShortArrayList(ShortArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(short element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(short element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, short element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Short> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Short> c) {
		Iterator<? extends Short> itr = c.iterator();
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
	public short set(int index, short element) {
		short prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(short o) {
		int index = indexOf(o);
		if (index != -1) {
			removeShort(index);
			return true;
		}
		return false;
	}

	@Override
	public short removeShort(int index) {
		short element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((short) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public short getShort(int index) {
		return array[index];
	}

	@Override
	public int indexOf(short element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(short element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(short element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((short) o))
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
	public ShortArrayList clone() {
		return new ShortArrayList(this);
	}

	@Override
	public Short[] toArray() {
		Short[] arr = new Short[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public short[] toShortArray() {
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
	public short[] toShortArray(short[] a) {
		if (a.length < size)
			a = new short[size];
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
		short[] newArray = new short[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			short[] newArray = new short[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
