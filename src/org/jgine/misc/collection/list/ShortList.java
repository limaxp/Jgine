package org.jgine.misc.collection.list;

import org.jgine.misc.collection.ShortCollection;

public interface ShortList extends ShortCollection, List<Short> {

	@Override
	@Deprecated
	public void add(int index, Short element);

	public void add(int index, short element);

	@Override
	@Deprecated
	public int insert(Short element);

	public int insert(short element);

	@Override
	public Short remove(int index);

	public short removeShort(int index);

	@Override
	@Deprecated
	public Short set(int index, Short element);

	public short set(int index, short element);

	@Override
	@Deprecated
	public Short get(int index);

	public short getShort(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(short element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(short element);
}
