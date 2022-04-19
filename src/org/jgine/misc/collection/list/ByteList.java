package org.jgine.misc.collection.list;

import org.jgine.misc.collection.ByteCollection;

public interface ByteList extends ByteCollection, List<Byte> {

	@Override
	@Deprecated
	public void add(int index, Byte element);

	public void add(int index, byte element);

	@Override
	@Deprecated
	public int insert(Byte element);

	public int insert(byte element);

	@Override
	public Byte remove(int index);

	public byte removeByte(int index);

	@Override
	@Deprecated
	public Byte set(int index, Byte element);

	public byte set(int index, byte element);

	@Override
	@Deprecated
	public Byte get(int index);

	public byte getByte(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(byte element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(byte element);
}
