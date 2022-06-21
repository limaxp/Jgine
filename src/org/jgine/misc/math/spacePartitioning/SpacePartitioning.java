package org.jgine.misc.math.spacePartitioning;

import java.util.List;

public interface SpacePartitioning<T> {

	public void set(double x, double y, T value);

	public default void set(double x, double y, double z, T value) {
		set(x, y, value);
	}

	public T get(double x, double y, T opt_default);

	public default T get(double x, double y, double z, T opt_default) {
		return get(x, y, opt_default);
	}

	public T remove(double x, double y);

	public default T remove(double x, double y, double z) {
		return remove(x, y);
	}

	public boolean contains(double x, double y);

	public default boolean contains(double x, double y, double z) {
		return contains(x, y);
	}

	public boolean isEmpty();

	public int getCount();

	public void clear();

	public List<T> getValues();
}
