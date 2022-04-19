package org.jgine.misc.utils.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.utils.logger.Logger;

public class GenIdRegistry<T> extends Registry<T> {

	protected final IdentityArrayList<T> values;
	protected final Map<String, T> keyMap;

	public GenIdRegistry(String name) {
		super(name);
		values = new IdentityArrayList<T>();
		keyMap = new HashMap<String, T>();
	}

	@Override
	public int register(String key, T value) {
		if (keyMap.containsKey(key)) {
			Logger.log(name + " Registry: key collision! key = " + key + ", value = " + value);
			return -1;
		}
		int id = values.insert(value);
		keyMap.put(key, value);
		return id;
	}

	@Override
	public boolean register(int id, String key, T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T get(String key) {
		return keyMap.get(key);
	}

	@Override
	public T getOrDefault(String key, T defaultValue) {
		return keyMap.getOrDefault(key, defaultValue);
	}

	@Override
	public T get(int id) {
		return values.get(id);
	}

	@Override
	public T getOrDefault(int id, T defaultValue) {
		T value = values.get(id);
		return value != null ? value : null;
	}

	@Override
	public T getByIndex(int index) {
		return values.get(index);
	}

	@Override
	public Collection<T> values() {
		return values;
	}

	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	@Override
	public int size() {
		return values.size();
	}
}