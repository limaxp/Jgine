package org.jgine.system.systems.input;

import java.util.function.Supplier;

public class InputHandlerType<T extends InputHandler> implements Supplier<T> {

	public final String name;
	private final Supplier<T> supplier;

	public InputHandlerType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
