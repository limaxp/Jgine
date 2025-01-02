package org.jgine.utils.collection.list;

import java.util.Collection;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * An ArrayList implementation that does instance checks instead of equals()
 * calls.
 */
public class IdentityArrayList<E> extends ObjectArrayList<E> implements List<E> {

	private static final long serialVersionUID = 7816190118094609361L;

	public IdentityArrayList() {
		super(8);
	}

	public IdentityArrayList(int capacity) {
		super(capacity);
	}

	public IdentityArrayList(Collection<? extends E> c) {
		super(c);
	}

	public IdentityArrayList(E[] array) {
		super(array);
	}

	@Override
	public int indexOf(Object o) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (a[i] == o)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size; i >= 0; i--)
			if (a[i] == o)
				return i;
		return -1;
	}

	@Override
	public IdentityArrayList<E> clone() {
		return (IdentityArrayList<E>) super.clone();
	}
}
