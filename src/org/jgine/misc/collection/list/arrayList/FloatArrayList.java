package org.jgine.misc.collection.list.arrayList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jgine.misc.collection.list.AbstractFloatList;
import org.jgine.misc.collection.list.FloatList;

/**
 * An Float ArrayList implementation that does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class FloatArrayList extends AbstractFloatList implements FloatList {

	protected float[] array;
	protected int size;

	public FloatArrayList() {
		this(FastArrayList.DEFAULT_CAPACITY);
	}

	public FloatArrayList(int capacity) {
		array = new float[capacity];
		size = 0;
	}

	public FloatArrayList(Collection<? extends Float> c) {
		this((int) (c.size() * 1.1f));
		addAll(c);
	}

	public FloatArrayList(float[] array) {
		this.array = array;
		size = array.length;
	}

	public FloatArrayList(float[] array, int size) {
		this.array = array;
		this.size = size;
	}

	public FloatArrayList(FloatArrayList orig) {
		array = orig.array.clone();
		size = orig.size;
	}

	@Override
	public boolean add(float element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		array[size++] = element;
		return true;
	}

	@Override
	public int insert(float element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		int index = size++;
		array[index] = element;
		return index;
	}

	@Override
	public void add(int index, float element) {
		if (size == array.length)
			ensureCapacity(size + 1);
		if (index != size)
			System.arraycopy(array, index, array, index + 1, size - index);
		array[index] = element;
		size++;
	}

	@Override
	public boolean addAll(Collection<? extends Float> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Float> c) {
		Iterator<? extends Float> itr = c.iterator();
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
	public float set(int index, float element) {
		float prevElement = array[index];
		array[index] = element;
		return prevElement;
	}

	@Override
	public boolean removeElement(float o) {
		int index = indexOf(o);
		if (index != -1) {
			removeFloat(index);
			return true;
		}
		return false;
	}

	@Override
	public float removeFloat(int index) {
		float element = array[index];
		if (index != --size)
			System.arraycopy(array, index + 1, array, index, size - index);
		array[size] = 0;
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean hasChanged = false;
		for (Object o : c)
			if (removeElement((float) o))
				hasChanged = true;
		return hasChanged;
	}

	@Override
	public float getFloat(int index) {
		return array[index];
	}

	@Override
	public int indexOf(float element) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(float element) {
		for (int i = size; i >= 0; i--)
			if (array[i] == element)
				return i;
		return -1;
	}

	@Override
	public boolean contains(float element) {
		return indexOf(element) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains((float) o))
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
	public FloatArrayList clone() {
		return new FloatArrayList(this);
	}

	@Override
	public Float[] toArray() {
		Float[] arr = new Float[size];
		System.arraycopy(array, 0, arr, 0, size);
		return arr;
	}

	@Override
	public float[] toFloatArray() {
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
	public float[] toFloatArray(float[] a) {
		if (a.length < size)
			a = new float[size];
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
		float[] newArray = new float[size];
		System.arraycopy(array, 0, newArray, 0, this.size);
		array = newArray;
	}

	public void trimToSize() {
		if (size != array.length) {
			float[] newArray = new float[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
