package org.jgine.system.systems.ui;

import java.util.function.Supplier;

public class UIObjectType<T extends UIObject> implements Supplier<T> {

	public final String name;
	private final Supplier<T> supplier;

	public UIObjectType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
