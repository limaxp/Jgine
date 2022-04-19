package org.jgine.misc.collection.list.arrayList;

import java.util.Collection;
import java.util.List;

/**
 * An ArrayList implementation that does instance checks instead of equals()
 * calls. Does NOT do range checks.
 * 
 * @author Maximilian Paar
 */
public class IdentityArrayList<E> extends FastArrayList<E> implements List<E> {

	public IdentityArrayList() {
		super();
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

	public IdentityArrayList(E[] array, int size) {
		super(array, size);
	}

	@Override
	public int indexOf(Object o) {
		int size = this.size;
		for (int i = 0; i < size; i++)
			if (array[i] == o)
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i = size; i >= 0; i--)
			if (array[i] == o)
				return i;
		return -1;
	}

	@Override
	public IdentityArrayList<E> clone() {
		return (IdentityArrayList<E>) super.clone();
	}
}
