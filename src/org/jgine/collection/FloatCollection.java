package org.jgine.collection;

import java.util.Collection;

public interface FloatCollection extends Collection<Float> {

	@Override
	@Deprecated
	public boolean add(Float element);

	public boolean add(float element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(float element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(float element);

	@Override
	@Deprecated
	public Object[] toArray();

	public float[] toFloatArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public float[] toFloatArray(float[] a);
}
