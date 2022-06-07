package org.jgine.misc.collection.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {

	private Object[] array;
	private int pos;

	public ArrayIterator(Object[] array) {
		this.array = array;
		this.pos = 0;
	}

	@Override
	public boolean hasNext() {
		return pos < array.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() throws NoSuchElementException {
		return (T) array[pos++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}