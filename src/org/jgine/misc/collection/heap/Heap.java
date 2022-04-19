package org.jgine.misc.collection.heap;

import java.util.List;

public interface Heap<E> extends List<E> {

	public void push(E item);

	public E pop();

	public E left(int index);

	public E right(int index);

	public E parent(int index);

	public void bubbleUp(E item);

	public void bubbleDown(E item);
}
