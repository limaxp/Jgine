package org.jgine.utils.math.spacePartitioning;

import java.util.function.Supplier;

/**
 * A type of {@link SpacePartitioning}.
 * 
 * @param <T> the type of {@link SpacePartitioning}
 */
public class SpacePartitioningType<T extends SpacePartitioning> implements Supplier<T> {

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public SpacePartitioningType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
