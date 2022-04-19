package org.jgine.misc.collection.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {


	private T[] array;
	private int pos;


	public ArrayIterator(T array[]) {
		this.array = array;
		this.pos = 0;
	}

	@Override
	public boolean hasNext() {
		return pos < array.length;
	}

	@Override
	public T next() throws NoSuchElementException {
		return array[pos++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}