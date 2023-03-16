package org.jgine.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.AbstractCharList;
import org.jgine.collection.list.CharList;

/**
 * An Char ArrayList implementation that does NOT do range checks.
 */
public class CharArrayList extends AbstractCharList implements CharList {

	protected char[] array;
	protected int size;

	public CharArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public CharArrayList(int capacity) {
		array = new char[capacity];
		size = 0;
	}

	public CharArrayList(Collection<? extends Character> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public CharArrayList(char[] array) {
		this.array = array;
		size = array.length;
	}

	public CharArrayList(char[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public CharArrayList(CharArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(char element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(char element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, char element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Character> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Character> c) {
		Iterator<? extends Character> itr = c.iterator();
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
	public char set(int index, char element) {
		char prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(char o) {
		int index = indexOf(o);
		if (index != -1) {
			removeChar(index);
			return true;
		}
		return false;
	}

	@Override
	public char removeChar(int index) {
		char element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((char) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public char getChar(int index) {
		return array[index];
	}

	@Override
	public int indexOf(char element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(char element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(char element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((char) o))
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
	public CharArrayList clone() {
		return new CharArrayList(this);
	}

	@Override
	public Character[] toArray() {
		Character[] arr = new Character[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public char[] toCharArray() {
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
	public char[] toCharArray(char[] a) {
		if (a.length < size)
			a = new char[size];
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
		char[] newArray = new char[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			char[] newArray = new char[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
