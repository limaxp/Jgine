package org.jgine.collection.list;

import org.jgine.collection.DoubleCollection;

public interface DoubleList extends DoubleCollection, List<Double> {

	@Override
	@Deprecated
	public void add(int index, Double element);

	public void add(int index, double element);

	@Override
	@Deprecated
	public int insert(Double element);

	public int insert(double element);

	@Override
	public Double remove(int index);

	public double removeDouble(int index);

	@Override
	@Deprecated
	public Double set(int index, Double element);

	public double set(int index, double element);

	@Override
	@Deprecated
	public Double get(int index);

	public double getDouble(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(double element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(double element);
}
