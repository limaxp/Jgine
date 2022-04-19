package org.jgine.misc.collection.list;

import org.jgine.misc.collection.IntCollection;

public interface IntList extends IntCollection, List<Integer> {

	@Override
	@Deprecated
	public void add(int index, Integer element);

	public void add(int index, int element);

	@Override
	@Deprecated
	public int insert(Integer element);

	public int insert(int element);

	@Override
	public Integer remove(int index);

	public int removeInt(int index);

	@Override
	@Deprecated
	public Integer set(int index, Integer element);

	public int set(int index, int element);

	@Override
	@Deprecated
	public Integer get(int index);

	public int getInt(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(int element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(int element);
}
