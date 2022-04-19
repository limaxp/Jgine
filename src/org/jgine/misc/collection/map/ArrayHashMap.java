package org.jgine.misc.collection.map;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArrayHashMap<K, V> extends HashMap<K, V[]> {

	private static final long serialVersionUID = 8092867687056028336L;

	public ArrayHashMap() {}

	public ArrayHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ArrayHashMap(Map<K, V[]> m) {
		super(m);
	}

	public ArrayHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, V value) {
		V[] array = get(key);
		if (array == null) {
			array = (V[]) Array.newInstance(value.getClass(), 1);
			array[0] = value;
		}
		else {
			int size = array.length;
			array = Arrays.copyOf(array, size + 1);
			array[size] = value;
		}
		put(key, array);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, V... values) {
		V[] array = get(key);
		if (array == null)
			array = values;
		else {
			array = Arrays.copyOf(array, array.length + values.length);
			System.arraycopy(values, 0, array, array.length, values.length);
		}
		put(key, array);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, Collection<V> values) {
		V[] array = get(key);
		if (array == null)
			array = (V[]) values.toArray();
		else {
			array = Arrays.copyOf(array, array.length + values.size());
			int i = 0;
			for (V value : values)
				array[array.length + i++] = value;
		}
		put(key, array);
	}

	public K rem(V value) {
		for (Entry<K, V[]> entry : entrySet()) {
			V[] array = entry.getValue();
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(value)) {
					K key = entry.getKey();
					put(key, rem(array, i));
					return key;
				}
			}
		}
		return null;
	}

	public void rem(K key, V value) {
		V[] array = get(key);
		if (array == null)
			return;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				put(key, rem(array, i));
				break;
			}
		}
	}

	public void rem(K key, int index) {
		V[] array = get(key);
		if (array == null)
			return;
		put(key, rem(array, index));
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
		V[] array = get(key);
		if (array == null)
			return;
		boolean changed = false;
		for (V value : values) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(value)) {
					array = rem(array, i);
					changed = true;
				}
			}
		}
		if (changed)
			put(key, array);
	}

	public void rem(K key, Collection<V> values) {
		V[] array = get(key);
		if (array == null)
			return;
		boolean changed = false;
		for (V value : values) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(value)) {
					array = rem(array, i);
					changed = true;
				}
			}
		}
		if (changed)
			put(key, array);
	}

	public V get(K key, int index) {
		return get(key)[index];
	}
}