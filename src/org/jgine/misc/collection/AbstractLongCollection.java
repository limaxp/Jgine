package org.jgine.misc.collection;

import java.util.AbstractCollection;

public abstract class AbstractLongCollection extends AbstractCollection<Long> implements LongCollection {

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
	public boolean remove(Object element) {
		return removeElement((long) element);
	}

	@Override
	public boolean removeElement(long element) {
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
	public long[] toLongArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long[] toLongArray(long[] a) {
		throw new UnsupportedOperationException();
	}
}
