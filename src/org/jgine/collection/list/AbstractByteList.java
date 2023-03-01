package org.jgine.collection.list;

import java.util.AbstractList;

public abstract class AbstractByteList extends AbstractList<Byte> implements ByteList {

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
	public void add(int index, Byte element) {
		add(index, (byte) element);
	}

	@Override
	public void add(int index, byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Byte element) {
		return insert((byte) element);
	}

	@Override
	public int insert(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((byte) element);
	}

	@Override
	public boolean removeElement(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Byte remove(int index) {
		return removeByte(index);
	}

	@Override
	public byte removeByte(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Byte set(int index, Byte element) {
		return set(index, (byte) element);
	}

	@Override
	public byte set(int index, byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Byte get(int index) {
		return getByte(index);
	}

	@Override
	public byte getByte(int index) {
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
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((byte) o);
	}

	@Override
	public int indexOf(byte element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((byte) o);
	}

	@Override
	public int lastIndexOf(byte element) {
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
