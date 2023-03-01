package org.jgine.collection;

import java.util.AbstractCollection;

public abstract class AbstractCharCollection extends AbstractCollection<Character> implements CharCollection {

	@Override
	@Deprecated
	public boolean add(Character element) {
		return add((char) element);
	}

	@Override
	public boolean add(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean remove(Object element) {
		return removeElement((char) element);
	}

	@Override
	public boolean removeElement(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((char) o);
	}

	@Override
	public boolean contains(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public char[] toCharArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public char[] toCharArray(char[] a) {
		throw new UnsupportedOperationException();
	}
}
