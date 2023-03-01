package org.jgine.collection.list;

import java.util.AbstractList;

public abstract class AbstractFloatList extends AbstractList<Float> implements FloatList {

	@Override
	@Deprecated
	public boolean add(Float element) {
		return add((float) element);
	}

	@Override
	public boolean add(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void add(int index, Float element) {
		add(index, (float) element);
	}

	@Override
	public void add(int index, float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Float element) {
		return insert((float) element);
	}

	@Override
	public int insert(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((int) element);
	}

	@Override
	public boolean removeElement(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Float remove(int index) {
		return removeFloat(index);
	}

	@Override
	public float removeFloat(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Float set(int index, Float element) {
		return set(index, (float) element);
	}

	@Override
	public float set(int index, float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Float get(int index) {
		return getFloat(index);
	}

	@Override
	public float getFloat(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((float) o);
	}

	@Override
	public boolean contains(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((float) o);
	}

	@Override
	public int indexOf(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((float) o);
	}

	@Override
	public int lastIndexOf(float element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] toFloatArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] toFloatArray(float[] a) {
		throw new UnsupportedOperationException();
	}
}
