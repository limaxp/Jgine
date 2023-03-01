package org.jgine.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.collection.list.AbstractByteList;
import org.jgine.collection.list.ByteList;

/**
 * An Float ArrayList implementation that does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class ByteArrayList extends AbstractByteList implements ByteList {

	protected byte[] array;
	protected int size;

	public ByteArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public ByteArrayList(int capacity) {
		array = new byte[capacity];
		size = 0;
	}

	public ByteArrayList(Collection<? extends Byte> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public ByteArrayList(byte[] array) {
		this.array = array;
		size = array.length;
	}

	public ByteArrayList(byte[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public ByteArrayList(ByteArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(byte element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(byte element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, byte element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Byte> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Byte> c) {
		Iterator<? extends Byte> itr = c.iterator();
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
	public byte set(int index, byte element) {
		byte prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(byte o) {
		int index = indexOf(o);
		if (index != -1) {
			removeByte(index);
			return true;
		}
		return false;
	}

	@Override
	public byte removeByte(int index) {
		byte element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((byte) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public byte getByte(int index) {
		return array[index];
	}

	@Override
	public int indexOf(byte element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(byte element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(byte element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((byte) o))
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
	public ByteArrayList clone() {
		return new ByteArrayList(this);
	}

	@Override
	public Byte[] toArray() {
		Byte[] arr = new Byte[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public byte[] toByteArray() {
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
	public byte[] toByteArray(byte[] a) {
		if (a.length < size)
			a = new byte[size];
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
		byte[] newArray = new byte[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			byte[] newArray = new byte[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
