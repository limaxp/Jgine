package org.jgine.collection;

import java.util.AbstractCollection;

public abstract class AbstractIntCollection extends AbstractCollection<Integer> implements IntCollection {

	@Override
	@Deprecated
	public boolean add(Integer element) {
		return add((int) element);
	}

	@Override
	public boolean add(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean remove(Object element) {
		return removeElement((int) element);
	}

	@Override
	public boolean removeElement(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((int) o);
	}

	@Override
	public boolean contains(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] toIntArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] toIntArray(int[] a) {
		throw new UnsupportedOperationException();
	}
}
