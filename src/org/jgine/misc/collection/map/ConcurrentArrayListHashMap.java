package org.jgine.misc.collection.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jgine.misc.collection.list.arrayList.FastArrayList;

public class ConcurrentArrayListHashMap<K, V> extends ConcurrentHashMap<K, List<V>> {

	private static final long serialVersionUID = 4184018242529988778L;

	public ConcurrentArrayListHashMap() {}

	public ConcurrentArrayListHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ConcurrentArrayListHashMap(Map<K, List<V>> m) {
		super(m);
	}

	public ConcurrentArrayListHashMap(int initialCapacity, float loadFactor) {
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

	public K rem(V value) {
		K key = null;
		synchronized (this) {
			for (Entry<K, List<V>> entry : entrySet()) {
				List<V> list = entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).equals(value)) {
						key = entry.getKey();
						break;
					}
				}
			}
		}
		if (key != null)
			get(key).remove(value);
		return key;
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
		List<V> list;
		synchronized (this) {
			list = super.get(key);
			if (list == null)
				put((K) key, list = Collections.synchronizedList(new FastArrayList<V>()));
		}
		return list;
	}

	public V get(K key, int index) {
		return get(key).get(index);
	}
}
