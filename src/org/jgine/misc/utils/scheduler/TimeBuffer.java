package org.jgine.misc.utils.scheduler;

import java.util.function.Consumer;

public class TimeBuffer<E> {

	protected long[] timeArray;
	protected Object[] objectArray;
	protected int size;

	public TimeBuffer(int capacity) {
		timeArray = new long[capacity];
		objectArray = new Object[capacity];
		size = 0;
	}

	public int add(long time, E element) {
		if (size == timeArray.length)
			ensureCapacity(size + 1);
		int index = getIndex(time);
		add(time, element, index);
		return index;
	}

	protected void add(long time, E element, int index) {
		if (index != 0 && index != size) {
			System.arraycopy(timeArray, index, timeArray, index + 1, size - index);
			System.arraycopy(objectArray, index, objectArray, index + 1, size - index);
		}
		timeArray[index] = time;
		objectArray[index] = element;
		size++;
	}

	public int remove(E element) {
		int index = getIndex(element);
		if (index != -1)
			remove(index);
		return index;
	}

	public void remove(int index) {
		size--;
		System.arraycopy(timeArray, index + 1, timeArray, index, size - index);
		System.arraycopy(objectArray, index + 1, objectArray, index, size - index);
	}

	public int getIndex(long time) {
		if (size == 0)
			return 0;
		if (time <= timeArray[0])
			return 0;
		if (time >= timeArray[size - 1])
			return size;
		return searchIndex(time);
	}

	protected int searchIndex(long time) {
		for (int i = 0; i < size; i++) {
			if (timeArray[i] > time)
				return i;
		}
		return -1;
	}

	public int getIndex(E element) {
		for (int i = 0; i < size; i++) {
			if (element == objectArray[i])
				return i;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public void update(long time, Consumer<E> func) {
		if (size == 0)
			return;
		if (time < timeArray[0])
			return;
		if (time >= timeArray[size - 1]) {
			for (int i = 0; i < size; i++)
				func.accept((E) objectArray[i]);
			this.size = 0;
			return;
		}
		int index = 0;
		for (; index < size; index++) {
			if (timeArray[index] > time)
				break;
			func.accept((E) objectArray[index]);
		}
		updateElements(index);
	}

	protected void updateElements(int index) {
		System.arraycopy(timeArray, index, timeArray, 0, size - index);
		System.arraycopy(objectArray, index, objectArray, 0, size - index);
		size -= index;
	}

	public void clear() {
		this.size = 0;
	}

	protected void ensureCapacity(int minCapacity) {
		int length = timeArray.length;
		if (minCapacity > length)
			resize(Math.max(length * 2, minCapacity));
	}

	protected void resize(int size) {
		long[] newArray = new long[size];
		System.arraycopy(timeArray, 0, newArray, 0, this.size);
		timeArray = newArray;

		Runnable[] newArray2 = new Runnable[size];
		System.arraycopy(objectArray, 0, newArray2, 0, this.size);
		objectArray = newArray2;
	}

	public void trimToSize() {
		if (size != timeArray.length) {
			long[] newArray = new long[size];
			System.arraycopy(timeArray, 0, newArray, 0, size);
			timeArray = newArray;

			Runnable[] newArray2 = new Runnable[size];
			System.arraycopy(objectArray, 0, newArray2, 0, size);
			objectArray = newArray2;
		}
	}

	public int size() {
		return size;
	}
}