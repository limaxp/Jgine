package org.jgine.misc.collection.list.arrayList.unordered;

import java.util.Collection;
import java.util.List;

/**
 * An ArrayList implementation that does instance checks instead of equals()
 * calls. Does NOT do range checks. Insert order is NOT persistent.
 * 
 * @author Maximilian Paar
 */
public class UnorderedIdentityArrayList<E> extends UnorderedArrayList<E> implements List<E> {

	public UnorderedIdentityArrayList() {
		super();
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

	public UnorderedIdentityArrayList(E[] array, int size) {
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
	public UnorderedIdentityArrayList<E> clone() {
		return (UnorderedIdentityArrayList<E>) super.clone();
	}
}
