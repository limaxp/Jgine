package org.jgine.collection.list;

import org.jgine.collection.LongCollection;

public interface LongList extends LongCollection, List<Long> {

	@Override
	@Deprecated
	public void add(int index, Long element);

	public void add(int index, long element);

	@Override
	@Deprecated
	public int insert(Long element);

	public int insert(long element);

	@Override
	public Long remove(int index);

	public long removeLong(int index);

	@Override
	@Deprecated
	public Long set(int index, Long element);

	public long set(int index, long element);

	@Override
	@Deprecated
	public Long get(int index);

	public long getLong(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(long element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(long element);
}
