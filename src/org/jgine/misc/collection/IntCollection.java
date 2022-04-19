package org.jgine.misc.collection;

import java.util.Collection;

public interface IntCollection extends Collection<Integer> {

	@Override
	@Deprecated
	public boolean add(Integer element);

	public boolean add(int element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(int element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(int element);

	@Override
	@Deprecated
	public Object[] toArray();

	public int[] toIntArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public int[] toIntArray(int[] a);
}
