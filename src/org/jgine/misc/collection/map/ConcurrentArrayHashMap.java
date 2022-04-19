package org.jgine.misc.collection.map;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentArrayHashMap<K, V> extends ConcurrentHashMap<K, V[]> {

	private static final long serialVersionUID = -5503312499439801666L;

	public ConcurrentArrayHashMap() {}

	public ConcurrentArrayHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ConcurrentArrayHashMap(Map<K, V[]> m) {
		super(m);
	}

	public ConcurrentArrayHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, V value) {
		V[] array;
		synchronized (this) {
			array = get(key);
			if (array == null) {
				array = (V[]) Array.newInstance(value.getClass(), 1);
				array[0] = value;
			}
			else {
				int size = array.length;
				array = Arrays.copyOf(array, size + 1);
				array[size] = value;
			}
		}
		put(key, array);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, V... values) {
		V[] array;
		synchronized (this) {
			array = get(key);
			if (array == null)
				array = values;
			else {
				array = Arrays.copyOf(array, array.length + values.length);
				System.arraycopy(values, 0, array, array.length, values.length);
			}
		}
		put(key, array);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, Collection<V> values) {
		V[] array;
		synchronized (this) {
			array = get(key);
			if (array == null)
				array = (V[]) values.toArray();
			else {
				array = Arrays.copyOf(array, array.length + values.size());
				int i = 0;
				for (V value : values)
					array[array.length + i++] = value;
			}
		}
		put(key, array);
	}

	public K rem(V value) {
		K key = null;
		V[] array = null;
		synchronized (this) {
			for (Entry<K, V[]> entry : entrySet()) {
				array = entry.getValue();
				for (int i = 0; i < array.length; i++) {
					if (array[i].equals(value)) {
						key = entry.getKey();
						array = rem(array, i);
						break;
					}
				}
			}
		}
		if (key != null)
			put(key, array);
		return key;
	}

	public void rem(K key, V value) {
		V[] array;
		boolean changed = false;
		synchronized (this) {
			array = get(key);
			if (array == null)
				return;
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(value)) {
					array = rem(array, i);
					changed = true;
					break;
				}
			}
		}
		if (changed)
			put(key, array);
	}

	public void rem(K key, int index) {
		V[] array;
		synchronized (this) {
			array = get(key);
			if (array == null)
				return;
			array = rem(array, index);
		}
		put(key, array);
	}

	private V[] rem(V[] array, int index) {
		int size = array.length - 1;
		@SuppressWarnings("unchecked")
		V[] newArray = (V[]) Array.newInstance(array[0].getClass(), size);
		if (index != size)
			System.arraycopy(array, index + 1, newArray, index, size - index);
		return newArray;
	}

	@SuppressWarnings("unchecked")
	public void rem(K key, V... values) {
		V[] array;
		boolean changed = false;
		synchronized (this) {
			array = get(key);
			if (array == null)
				return;
			for (V value : values) {
				for (int i = 0; i < array.length; i++) {
					if (array[i].equals(value)) {
						array = rem(array, i);
						changed = true;
					}
				}
			}
		}
		if (changed)
			put(key, array);
	}

	public void rem(K key, Collection<V> values) {
		V[] array;
		boolean changed = false;
		synchronized (this) {
			array = get(key);
			if (array == null)
				return;
			for (V value : values) {
				for (int i = 0; i < array.length; i++) {
					if (array[i].equals(value)) {
						array = rem(array, i);
						changed = true;
					}
				}
			}
		}
		if (changed)
			put(key, array);
	}

	public V get(K key, int index) {
		synchronized (this) {
			return get(key)[index];
		}
	}
}
