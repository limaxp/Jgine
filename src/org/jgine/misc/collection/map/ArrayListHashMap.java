package org.jgine.misc.collection.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.collection.list.arrayList.FastArrayList;

public class ArrayListHashMap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = 8092867687056028336L;

	public ArrayListHashMap() {
	}

	public ArrayListHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ArrayListHashMap(Map<K, List<V>> m) {
		super(m);
	}

	public ArrayListHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public void add(K key, V value) {
		get(key).add(value);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, V... values) {
		get(key).addAll(Arrays.asList(values));
	}

	public void add(K key, Collection<V> values) {
		get(key).addAll(values);
	}

	@Nullable
	public K rem(V value) {
		for (Entry<K, List<V>> entry : entrySet()) {
			List<V> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(value)) {
					K key = entry.getKey();
					get(key).remove(value);
					return key;
				}
			}
		}
		return null;
	}

	public void rem(K key, V value) {
		get(key).remove(value);
	}

	public void rem(K key, int index) {
		get(key).remove(index);
	}

	@SuppressWarnings("unchecked")
	public void rem(K key, V... values) {
		get(key).removeAll(Arrays.asList(values));
	}

	public void rem(K key, Collection<V> values) {
		get(key).removeAll(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<V> get(Object key) {
		List<V> list = super.get(key);
		if (list == null)
			put((K) key, list = new FastArrayList<V>());
		return list;
	}

	@Nullable
	public V get(K key, int index) {
		List<V> list = get(key);
		if (list == null)
			return null;
		return list.get(index);
	}
}
