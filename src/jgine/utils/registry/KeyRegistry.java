package jgine.utils.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Registry;
import jgine.utils.Logger;
import jgine.utils.collection.ArrayIterator;

/**
 * {@link Registry} implementation that only uses name for registration. Uses
 * entry index as id. Max id = size - 1.
 * 
 * @param <T> the type of elements in this registry
 */
public final class KeyRegistry<T> extends Registry<T> {

	protected final Object[] values;
	protected int size;
	protected final Map<String, T> keyMap;

	public KeyRegistry(String name, int size) {
		super(name);
		values = new Object[size];
		keyMap = new HashMap<String, T>(size);
	}

	// for main registry
	public KeyRegistry(int size) {
		super();
		values = new Object[size];
		keyMap = new HashMap<String, T>(size);
	}

	@Override
	public int register(String key, T value) {
		if (keyMap.containsKey(key)) {
			Logger.warn(name + " Registry: key collision! key = " + key + ", value = " + value);
			return -1;
		}
		int id = size++;
		values[id] = value;
		keyMap.put(key, value);
		return id;
	}

	@Deprecated
	@Override
	public boolean register(int id, String key, T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @Nullable T get(String key) {
		return keyMap.get(key);
	}

	@Override
	public T getOrDefault(String key, T defaultValue) {
		return keyMap.getOrDefault(key, defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public @Nullable T get(int id) {
		return (T) values[id];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getOrDefault(int id, T defaultValue) {
		Object value = values[id];
		return value != null ? (T) value : defaultValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public @Nullable T getAt(int index) {
		return (T) values[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getAtOrDefault(int index, T defaultValue) {
		Object value = values[index];
		return value != null ? (T) value : defaultValue;
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
	public Collection<T> values() {
		return keyMap.values();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int capacity() {
		return values.length;
	}
}