package org.jgine.misc.collection;

import java.util.Collection;

public interface DoubleCollection extends Collection<Double> {

	@Override
	@Deprecated
	public boolean add(Double element);

	public boolean add(double element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(double element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(double element);

	@Override
	@Deprecated
	public Object[] toArray();

	public double[] toDoubleArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public double[] toDoubleArray(double[] a);
}
