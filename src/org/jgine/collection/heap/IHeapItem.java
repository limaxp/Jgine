package org.jgine.collection.heap;

public interface IHeapItem<T> extends Comparable<T> {

	public int getHeapIndex();

	public void setHeapIndex(int index);

}