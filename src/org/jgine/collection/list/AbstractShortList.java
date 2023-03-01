package org.jgine.collection.list;

import java.util.AbstractList;

public abstract class AbstractShortList extends AbstractList<Short> implements ShortList {

	@Override
	@Deprecated
	public boolean add(Short element) {
		return add((short) element);
	}

	@Override
	public boolean add(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void add(int index, Short element) {
		add(index, (short) element);
	}

	@Override
	public void add(int index, short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Short element) {
		return insert((short) element);
	}

	@Override
	public int insert(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((short) element);
	}

	@Override
	public boolean removeElement(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Short remove(int index) {
		return removeShort(index);
	}

	@Override
	public short removeShort(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Short set(int index, Short element) {
		return set(index, (short) element);
	}

	@Override
	public short set(int index, short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Short get(int index) {
		return getShort(index);
	}

	@Override
	public short getShort(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((short) o);
	}

	@Override
	public boolean contains(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((short) o);
	}

	@Override
	public int indexOf(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((short) o);
	}

	@Override
	public int lastIndexOf(short element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public short[] toShortArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public short[] toShortArray(short[] a) {
		throw new UnsupportedOperationException();
	}
}
