package org.jgine.system.systems.light;

import java.util.function.Supplier;

import org.jgine.render.light.Light;

public class LightType<T extends Light> implements Supplier<T> {

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public LightType(String name, Supplier<T> supplier) {
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
