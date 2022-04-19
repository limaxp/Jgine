package org.jgine.misc.collection;

import java.util.AbstractCollection;

public abstract class AbstractShortCollection extends AbstractCollection<Short> implements ShortCollection {

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
	public boolean remove(Object element) {
		return removeElement((short) element);
	}

	@Override
	public boolean removeElement(short element) {
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
	public short[] toShortArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public short[] toShortArray(short[] a) {
		throw new UnsupportedOperationException();
	}
}
