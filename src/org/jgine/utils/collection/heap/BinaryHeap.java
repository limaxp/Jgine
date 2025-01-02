package org.jgine.utils.collection.heap;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Represents a binary heap that is based on an array.
 * 
 * @param <E> The type the tree contains.
 */
public abstract class BinaryHeap<E extends IHeapItem<E>> extends AbstractHeap<E> {

	protected E[] array;
	public int length;

	public BinaryHeap(E[] array) {
		this.array = array;
		length = 0;
	}

	private void resizeArray() {
		array = Arrays.copyOf(array, array.length + 10);
	}

	@Override
	public void push(E item) {
		item.setHeapIndex(length);
		if (array.length <= length)
			resizeArray();
		array[length] = item; // set to last point
		bubbleUp(item);
		length++;
	}

	@Override
	public E pop() {
		E firstItem = array[0]; // get first element
		length--;
		array[0] = array[length]; // set the last to the first element
		array[0].setHeapIndex(0);
		bubbleDown(array[0]);
		return firstItem;
	}

	@Override
	public E left(int index) {
		return array[2 * index + 1];
	}

	@Override
	public E right(int index) {
		return array[2 * index + 2];
	}

	@Override
	public E parent(int index) {
		return array[(index - 1) / 2];
	}

	protected void swap(E itemA, E itemB) {
		int heapIndexA = itemA.getHeapIndex();
		int heapIndexB = itemB.getHeapIndex();
		array[heapIndexA] = itemB;
		array[heapIndexB] = itemA;
		int tmpIndex = heapIndexA;
		itemA.setHeapIndex(heapIndexB);
		itemB.setHeapIndex(tmpIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object arg0) {
		return this.array[((E) arg0).getHeapIndex()] == arg0;
	}

	@Override
	public E get(int index) {
		return this.array[index];
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public Iterator<E> iterator() {
		return new BinaryHeapIterator();
	}

	@Override
	public void clear() {
		for (int i = 0; i < length; i++)
			array[i] = null;
		length = 0;
	}

	public int getArrayLength() {
		return array.length;
	}

	public static class BinaryMinHeap<E extends IHeapItem<E>> extends BinaryHeap<E> {

		public BinaryMinHeap(E[] array) {
			super(array);
		}

		@Override
		public void bubbleUp(E item) {
			while (true) {
				E parentItem = array[(item.getHeapIndex() - 1) / 2];
				E neighborItem;
				if (parentItem != null && parentItem != item) {
					neighborItem = left(parentItem.getHeapIndex());
					// if(neighborItem == item)
					// neighborItem = right(parentItem.getHeapIndex());
					if (neighborItem != null && item.compareTo(neighborItem) < 0) {
						swap(item, neighborItem);
					}
					if (parentItem != null && item.compareTo(parentItem) < 0) {
						swap(item, parentItem);
					} else
						return;
				} else
					return;
			}
		}

		@Override
		public void bubbleDown(E item) {
			while (true) {
				int leftI = item.getHeapIndex() * 2 + 1;
				int rightI = item.getHeapIndex() * 2 + 2;
				int swapI = 0;

				if (leftI < length) {
					swapI = leftI;
					if (rightI < length && array[rightI].compareTo(array[leftI]) < 0)
						swapI = rightI;

					if (item.compareTo(array[swapI]) > 0)
						swap(item, array[swapI]);
					else
						return;
				} else
					return;
			}
		}
	}

	public static class BinaryMaxHeap<E extends IHeapItem<E>> extends BinaryHeap<E> {

		// TODO implement this!

		public BinaryMaxHeap(E[] array) {
			super(array);
		}

		@Override
		public void bubbleUp(E item) {
			return;
		}

		@Override
		public void bubbleDown(E item) {
			return;
		}
	}

	private class BinaryHeapIterator implements Iterator<E> {

		private int index;

		private BinaryHeapIterator() {
		}

		@Override
		public boolean hasNext() {
			if (length > index)
				return true;
			return false;
		}

		@Override
		public @NonNullByDefault E next() {
			return array[index++];
		}
	}
}
