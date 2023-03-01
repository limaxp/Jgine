package org.jgine.collection.list;

import org.jgine.collection.FloatCollection;

public interface FloatList extends FloatCollection, List<Float> {

	@Override
	@Deprecated
	public void add(int index, Float element);

	public void add(int index, float element);

	@Override
	@Deprecated
	public int insert(Float element);

	public int insert(float element);

	@Override
	public Float remove(int index);

	public float removeFloat(int index);

	@Override
	@Deprecated
	public Float set(int index, Float element);

	public float set(int index, float element);

	@Override
	@Deprecated
	public Float get(int index);

	public float getFloat(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(float element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(float element);

}
