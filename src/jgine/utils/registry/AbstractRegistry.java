package jgine.utils.registry;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A map of types identified by <code>int</code> id and {@link String} key.
 * Implements {@link Iterable}, has a size and can also be accessed by index or
 * a value collection directly.
 * <p>
 * <strong> All registers should be done on startup otherwise not thread
 * safe!</strong> Supports up to 1000 different registries.
 * 
 * @param <T> the type of elements in this registry
 */
public abstract class AbstractRegistry<T> implements Iterable<T> {

	public static final KeyRegistry<AbstractRegistry<?>> REGISTRY = new KeyRegistry<AbstractRegistry<?>>(1000);

	public final int id;
	public final String name;

	public AbstractRegistry(String name) {
		this.id = REGISTRY.register(name, this);
		this.name = name;
	}

	AbstractRegistry() { // for main registry
		this.id = -1;
		this.name = "registry";
	}

	public abstract int register(String key, T value);

	public abstract boolean register(int id, String key, T value);

	public abstract @Nullable T get(String key);

	public abstract T getOrDefault(String key, T defaultValue);

	public abstract @Nullable T get(int id);

	public abstract T getOrDefault(int id, T defaultValue);

	public abstract @Nullable T getAt(int index);

	public abstract T getAtOrDefault(int index, T defaultValue);

	public abstract Collection<T> values();

	public abstract int size();

	public abstract int capacity();
}
