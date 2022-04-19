package org.jgine.misc.collection;

import java.util.AbstractCollection;

public abstract class AbstractByteCollection extends AbstractCollection<Byte> implements ByteCollection {

	@Override
	@Deprecated
	public boolean add(Byte element) {
		return add((byte) element);
	}

	@Override
	public boolean add(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean remove(Object element) {
		return removeElement((byte) element);
	}

	@Override
	public boolean removeElement(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((byte) o);
	}

	@Override
	public boolean contains(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] toByteArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] toByteArray(byte[] a) {
		throw new UnsupportedOperationException();
	}
}
