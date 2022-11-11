package org.jgine.system;

import java.util.Map;

import org.jgine.core.Scene;

public abstract class EngineSystem {

	public final String name;
	private int id;

	public EngineSystem(String name) {
		this.id = -1;
		this.name = name;
	}

	public abstract SystemScene<?, ?> createScene(Scene scene);

	public abstract SystemObject load(Map<String, Object> data);

	public void init(int id) {
		if (this.id != -1)
			return;
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	public int getId() {
		return id;
	}
}
