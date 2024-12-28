package org.jgine.utils.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.jgine.collection.ArrayIterator;
import org.jgine.utils.logger.Logger;

/**
 * The default {@link Registry}. Uses given id and name for registration. Only
 * passing name will use its hash code as id.
 * 
 * @param <T> the type of elements in this registry
 */
public class BaseRegistry<T> extends Registry<T> {

	protected final Object[] values;
	protected int size;
	protected final Object[] idMap;
	protected final Map<String, T> keyMap;

	public BaseRegistry(String name, int size) {
		super(name);
		values = new Object[size];
		idMap = new Object[size];
		keyMap = new HashMap<String, T>(size);
	}

	/**
	 * Uses hashCode() of key as id!
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
		if (idMap[id] != null) {
			Logger.log(name + " Registry: id collision! id = " + id + ", value = " + value);
			return false;
		}
		if (keyMap.containsKey(key)) {
			Logger.log(name + " Registry: key collision! key = " + key + ", value = " + value);
			return false;
		}
		values[size++] = value;
		idMap[id] = value;
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

	@SuppressWarnings("unchecked")
	@Override
	public T get(int id) {
		return (T) idMap[id];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getOrDefault(int id, T defaultValue) {
		Object value = idMap[id];
		return value != null ? (T) value : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getByIndex(int index) {
		return (T) values[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public void forEach(Consumer<? super T> action) {
		for (int i = 0; i < size; i++)
			action.accept((T) values[i]);
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<T>(values);
	}

	@Override
	public int size() {
		return size;
	}
}
