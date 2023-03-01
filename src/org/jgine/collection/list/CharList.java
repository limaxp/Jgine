package org.jgine.collection.list;

import org.jgine.collection.CharCollection;

public interface CharList extends CharCollection, List<Character> {

	@Override
	@Deprecated
	public void add(int index, Character element);

	public void add(int index, char element);

	@Override
	@Deprecated
	public int insert(Character element);

	public int insert(char element);

	@Override
	public Character remove(int index);

	public char removeChar(int index);

	@Override
	@Deprecated
	public Character set(int index, Character element);

	public char set(int index, char element);

	@Override
	@Deprecated
	public Character get(int index);

	public char getChar(int index);

	@Override
	@Deprecated
	public int indexOf(Object o);

	public int indexOf(char element);

	@Override
	@Deprecated
	public int lastIndexOf(Object o);

	public int lastIndexOf(char element);
}
