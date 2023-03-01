package org.jgine.collection;

import java.util.Collection;

public interface ByteCollection extends Collection<Byte> {

	@Override
	@Deprecated
	public boolean add(Byte element);

	public boolean add(byte element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(byte element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(byte element);

	@Override
	@Deprecated
	public Object[] toArray();

	public byte[] toByteArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public byte[] toByteArray(byte[] a);
}
