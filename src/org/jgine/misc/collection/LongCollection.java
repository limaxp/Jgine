package org.jgine.misc.collection;

import java.util.Collection;

public interface LongCollection extends Collection<Long> {

	@Override
	@Deprecated
	public boolean add(Long element);

	public boolean add(long element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(long element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(long element);

	@Override
	@Deprecated
	public Object[] toArray();

	public long[] toLongArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public long[] toLongArray(long[] a);
}
