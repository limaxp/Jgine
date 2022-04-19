package org.jgine.misc.collection;

import java.util.Collection;

public interface ShortCollection extends Collection<Short> {

	@Override
	@Deprecated
	public boolean add(Short element);

	public boolean add(short element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(short element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(short element);

	@Override
	@Deprecated
	public Object[] toArray();

	public short[] toShortArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public short[] toShortArray(short[] a);
}
