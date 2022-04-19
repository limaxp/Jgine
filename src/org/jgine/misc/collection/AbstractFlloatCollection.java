package org.jgine.misc.collection;

import java.util.AbstractCollection;

public abstract class AbstractFlloatCollection extends AbstractCollection<Float> implements FloatCollection {

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
	public boolean remove(Object element) {
		return removeElement((float) element);
	}

	@Override
	public boolean removeElement(float element) {
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
	public float[] toFloatArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] toFloatArray(float[] a) {
		throw new UnsupportedOperationException();
	}
}
