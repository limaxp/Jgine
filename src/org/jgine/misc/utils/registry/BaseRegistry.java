package org.jgine.misc.utils.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.utils.logger.Logger;

public class BaseRegistry<T> extends Registry<T> {

	protected final IdentityArrayList<T> values;
	protected final Map<Integer, T> idMap;
	protected final Map<String, T> keyMap;

	public BaseRegistry(String name) {
		super(name);
		values = new IdentityArrayList<T>();
		idMap = new HashMap<Integer, T>();
		keyMap = new HashMap<String, T>();
	}

	/**
	 * Uses hashCode() of key to generate id!
	 */
	@Override
	public int register(String key, T value) {
		int id = key.hashCode();
		if (!register(id, key, value))
			return -1;
		return id;
	}

	@Override
	public boolean register(int id, String key, T value) {
		if (idMap.containsKey(id)) {
			Logger.log(name + " Registry: id collision! id = " + id + ", value = " + value);
			return false;
		}
		if (keyMap.containsKey(key)) {
			Logger.log(name + " Registry: key collision! key = " + key + ", value = " + value);
			return false;
		}
		values.add(value);
		idMap.put(id, value);
		keyMap.put(key, value);
		return true;
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
		return idMap.get(id);
	}

	@Override
	public T getOrDefault(int id, T defaultValue) {
		return idMap.getOrDefault(id, defaultValue);
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
		return values().iterator();
	}

	@Override
	public int size() {
		return values.size();
	}
}
