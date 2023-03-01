package org.jgine.collection;

import java.util.Collection;

public interface CharCollection extends Collection<Character> {

	@Override
	@Deprecated
	public boolean add(Character element);

	public boolean add(char element);

	@Override
	@Deprecated
	public boolean remove(Object element);

	public boolean removeElement(char element);

	@Override
	@Deprecated
	public boolean contains(Object o);

	public boolean contains(char element);

	@Override
	@Deprecated
	public Object[] toArray();

	public char[] toCharArray();

	@Override
	@Deprecated
	public <T> T[] toArray(T[] a);

	public char[] toCharArray(char[] a);
}
