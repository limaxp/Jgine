package org.jgine.system.systems.script;

import java.util.function.Supplier;

public class ScriptType<T extends ScriptObjectJava> implements Supplier<T> {

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public ScriptType(String name, Supplier<T> supplier) {
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
