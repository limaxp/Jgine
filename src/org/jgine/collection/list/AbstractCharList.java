package org.jgine.collection.list;

import java.util.AbstractList;

public abstract class AbstractCharList extends AbstractList<Character> implements CharList {

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
	public void add(int index, Character element) {
		add(index, (char) element);
	}

	@Override
	public void add(int index, char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Character element) {
		return insert((char) element);
	}

	@Override
	public int insert(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((char) element);
	}

	@Override
	public boolean removeElement(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Character remove(int index) {
		return removeChar(index);
	}

	@Override
	public char removeChar(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Character set(int index, Character element) {
		return set(index, (char) element);
	}

	@Override
	public char set(int index, char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Character get(int index) {
		return getChar(index);
	}

	@Override
	public char getChar(int index) {
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
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((char) o);
	}

	@Override
	public int indexOf(char element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((char) o);
	}

	@Override
	public int lastIndexOf(char element) {
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
