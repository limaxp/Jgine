package org.jgine.misc.collection.list;

import java.util.AbstractList;

public abstract class AbstractLongList extends AbstractList<Long> implements LongList {

	@Override
	@Deprecated
	public boolean add(Long element) {
		return add((long) element);
	}

	@Override
	public boolean add(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void add(int index, Long element) {
		add(index, (long) element);
	}

	@Override
	public void add(int index, long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Long element) {
		return insert((long) element);
	}

	@Override
	public int insert(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((long) element);
	}

	@Override
	public boolean removeElement(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Long remove(int index) {
		return removeLong(index);
	}

	@Override
	public long removeLong(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Long set(int index, Long element) {
		return set(index, (long) element);
	}

	@Override
	public long set(int index, long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Long get(int index) {
		return getLong(index);
	}

	@Override
	public long getLong(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((long) o);
	}

	@Override
	public boolean contains(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((long) o);
	}

	@Override
	public int indexOf(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((long) o);
	}

	@Override
	public int lastIndexOf(long element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long[] toLongArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long[] toLongArray(long[] a) {
		throw new UnsupportedOperationException();
	}
}
