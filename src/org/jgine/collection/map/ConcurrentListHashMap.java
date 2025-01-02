package org.jgine.collection.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.Nullable;

public class ConcurrentListHashMap<K, V> extends ConcurrentHashMap<K, List<V>> {

	private static final long serialVersionUID = 4184018242529988778L;

	public ConcurrentListHashMap() {
	}

	public ConcurrentListHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ConcurrentListHashMap(Map<K, List<V>> m) {
		super(m);
	}

	public ConcurrentListHashMap(int initialCapacity, float loadFactor) {
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
				put((K) key, list = Collections.synchronizedList(new ArrayList<V>()));
		}
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
