package org.jgine.utils.collection.queue;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FixedSizeArrayDeque<E> extends AbstractQueue<E> implements Deque<E> {

	protected final E[] array;
	protected int size;

	@SuppressWarnings("unchecked")
	public FixedSizeArrayDeque(int size) {
		if (size < 1)
			throw new IllegalArgumentException("Size must be higher then 0");
		array = (E[]) new Object[size + 1];
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		Iterator<? extends E> itr = c.iterator();
		int addSize = c.size();

		if (addSize > freeSize())
			return false;
		int lastIndex = index + addSize;
		if (size > 0 && index != size)
			System.arraycopy(array, index + 1, array, lastIndex + 1, size - index);
		for (; index < lastIndex; index++)
			array[index + 1] = itr.next();
		size += addSize;
		return addSize > 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++)
			array[i + 1] = null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean isEmpty() {
		return size < 1;
	}

	public boolean isFull() {
		return size + 1 >= array.length;
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
	public Object[] toArray() {
		return Arrays.copyOfRange(array, 1, size + 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		else if (a.length > size)
			a[size] = null;
		System.arraycopy(array, 1, a, 1, size + 1);
		return a;
	};

	@Override
	public boolean add(E e) {
		if (isFull())
			return false;
		addLast(e);
		return true;
	}

	@Override
	public void addFirst(E e) {
		offerFirst(e);
	}

	@Override
	public void addLast(E e) {
		offerLast(e);
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	public int indexOf(Object o) {
		for (int i = 0; i < size; i++)
			if (array[i + 1].equals(o))
				return i;
		return -1;
	}

	public int lastIndexOf(Object o) {
		for (int i = size; i >= 0; i--)
			if (array[i + 1].equals(o))
				return i;
		return -1;
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}

	@Override
	public E element() {
		return getFirst();
	}

	@Override
	public E getFirst() {
		if (isEmpty())
			throw new NoSuchElementException();
		return peekFirst();
	}

	@Override
	public E getLast() {
		if (isEmpty())
			throw new NoSuchElementException();
		return peekLast();
	}

	@Override
	public Iterator<E> iterator() {
		return new DeqIterator();
	}

	@Override
	public boolean offer(E e) {
		return offerLast(e);
	}

	@Override
	public boolean offerFirst(E e) {
		if (!isFull()) {
			array[++size] = e;
			return true;
		}
		return false;
	}

	@Override
	public boolean offerLast(E e) {
		if (!isFull()) {
			if (!isEmpty())
				System.arraycopy(array, 1, array, 2, size++);
			array[0] = e;
			return true;
		}
		return false;
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	@Override
	public E peekFirst() {
		return array[size];
	}

	@Override
	public E peekLast() {
		return array[1];
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E pollFirst() {
		if (!isEmpty())
			return array[size--];
		return null;
	}

	@Override
	public E pollLast() {
		if (!isEmpty()) {
			E value = array[0];
			System.arraycopy(array, 2, array, 1, --size);
			return value;
		}
		return null;
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public E remove() {
		return removeFirst();
	}

	@Override
	public boolean remove(Object o) {
		return removeFirstOccurrence(o);
	}

	@Override
	public E removeFirst() {
		if (isEmpty())
			throw new NoSuchElementException();
		return pollFirst();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		int index = indexOf(o);
		if (index != -1) {
			remove(index);
			return true;
		}
		return false;
	}

	@Override
	public E removeLast() {
		if (isEmpty())
			throw new NoSuchElementException();
		return pollLast();
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		int index = lastIndexOf(o);
		if (index != -1) {
			remove(index);
			return true;
		}
		return false;
	}

	public E remove(int index) {
		E element = array[index + 1];
		if (index != --size)
			System.arraycopy(array, index + 2, array, index + 1, size - index);
		array[size + 1] = null;
		return element;
	}

	@Override
	public int size() {
		return size;
	}

	public int freeSize() {
		return array.length - (size + 1);
	}

	private class DeqIterator implements Iterator<E> {

		int cursor;
		int lastRet = -1;

		@Override
		public boolean hasNext() {
			return cursor < size;
		}

		@Override
		public E next() {
			lastRet = cursor;
			return array[++cursor];
		}

		void postDelete() {
			cursor--;
		}

		@Override
		public final void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			FixedSizeArrayDeque.this.remove(lastRet);
			postDelete();
			lastRet = -1;
		}
	}

	private class DescendingIterator extends DeqIterator {

		DescendingIterator() {
			cursor = size - 1;
		}

		@Override
		public boolean hasNext() {
			return cursor > 0;
		}

		@Override
		public final E next() {
			lastRet = cursor;
			return array[--cursor];
		}

		@Override
		void postDelete() {}
	}
}
