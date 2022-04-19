package org.jgine.misc.collection.list;

import java.util.AbstractList;

public abstract class AbstractDoubleList extends AbstractList<Double> implements DoubleList {

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
	public void add(int index, Double element) {
		add(index, (double) element);
	}

	@Override
	public void add(int index, double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int insert(Double element) {
		return insert((double) element);
	}

	@Override
	public int insert(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object element) {
		return removeElement((double) element);
	}

	@Override
	public boolean removeElement(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Double remove(int index) {
		return removeDouble(index);
	}

	@Override
	public double removeDouble(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Double set(int index, Double element) {
		return set(index, (double) element);
	}

	@Override
	public double set(int index, double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public Double get(int index) {
		return getDouble(index);
	}

	@Override
	public double getDouble(int index) {
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
	@Deprecated
	public int indexOf(Object o) {
		return indexOf((double) o);
	}

	@Override
	public int indexOf(double element) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public int lastIndexOf(Object o) {
		return lastIndexOf((double) o);
	}

	@Override
	public int lastIndexOf(double element) {
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
