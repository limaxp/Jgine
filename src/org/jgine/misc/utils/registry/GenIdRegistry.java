package org.jgine.misc.utils.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.jgine.misc.collection.iterator.ArrayIterator;
import org.jgine.misc.utils.logger.Logger;

public class GenIdRegistry<T> extends Registry<T> {

	protected final Object[] values;
	protected int size;
	protected final Map<String, T> keyMap;

	public GenIdRegistry(String name, int size) {
		super(name);
		values = new Object[size];
		keyMap = new HashMap<String, T>(size);
	}

	@Override
	public int register(String key, T value) {
		if (keyMap.containsKey(key)) {
			Logger.log(name + " Registry: key collision! key = " + key + ", value = " + value);
			return -1;
		}
		int id = size++;
		values[id] = value;
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

	@SuppressWarnings("unchecked")
	@Override
	public T get(int id) {
		return (T) values[id];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getOrDefault(int id, T defaultValue) {
		Object value = values[id];
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