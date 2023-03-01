package org.jgine.collection;

import java.util.AbstractCollection;

public abstract class AbstractDoubleCollection extends AbstractCollection<Double> implements DoubleCollection {

	@Override
	@Deprecated
	public boolean add(Double element) {
		return add((double) element);
	}

	@Override
	public boolean add(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean remove(Object element) {
		return removeElement((double) element);
	}

	@Override
	public boolean removeElement(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean contains(Object o) {
		return contains((double) o);
	}

	@Override
	public boolean contains(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double[] toDoubleArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double[] toDoubleArray(double[] a) {
		throw new UnsupportedOperationException();
	}
}
