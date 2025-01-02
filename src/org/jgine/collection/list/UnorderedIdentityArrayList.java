package org.jgine.collection.list;

import java.util.Collection;
import java.util.List;

/**
 * An ArrayList implementation that does instance checks instead of equals()
 * calls. Insert order is NOT persistent.
 */
public class UnorderedIdentityArrayList<E> extends UnorderedArrayList<E> implements List<E> {

	private static final long serialVersionUID = 7583659128432771258L;

	public UnorderedIdentityArrayList() {
		super(8);
	}

	public UnorderedIdentityArrayList(int capacity) {
		super(capacity);
	}

	public UnorderedIdentityArrayList(Collection<? extends E> c) {
		super(c);
	}

	public UnorderedIdentityArrayList(E[] array) {
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
	public UnorderedIdentityArrayList<E> clone() {
		return (UnorderedIdentityArrayList<E>) super.clone();
	}
}
