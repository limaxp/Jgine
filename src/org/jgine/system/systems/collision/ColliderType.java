package org.jgine.system.systems.collision;

import java.util.function.Supplier;

public class ColliderType<T extends Collider> implements Supplier<T> {

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public ColliderType(String name, Supplier<T> supplier) {
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
