package org.jgine.system;

import java.util.Map;

import org.jgine.core.Scene;

public abstract class EngineSystem {

	private int id;
	private String name;

	public abstract SystemScene<?, ?> createScene(Scene scene);

	public abstract SystemObject load(Map<String, Object> data);

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	public void init(int id, String name) {
		if (this.name != null)
			return;
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
