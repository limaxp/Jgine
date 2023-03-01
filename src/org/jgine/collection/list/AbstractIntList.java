package org.jgine.collection.list;

import java.util.AbstractList;

public abstract class AbstractIntList extends AbstractList<Integer> implements IntList {

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
	public void add(int index, Integer element) {
		add(index, (int) element);
	}

	@Override
	public void add(int index, int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Integer element) {
		return insert((int) element);
	}

	@Override
	public int insert(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((int) element);
	}

	@Override
	public boolean removeElement(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Integer remove(int index) {
		return removeInt(index);
	}

	@Override
	public int removeInt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Integer set(int index, Integer element) {
		return set(index, (int) element);
	}

	@Override
	public int set(int index, int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Integer get(int index) {
		return getInt(index);
	}

	@Override
	public int getInt(int index) {
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
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((int) o);
	}

	@Override
	public int indexOf(int element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((int) o);
	}

	@Override
	public int lastIndexOf(int element) {
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
