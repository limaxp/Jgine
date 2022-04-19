package org.jgine.system;

import java.util.HashMap;
import java.util.Map;

import org.jgine.core.Scene;

public abstract class EngineSystem {

	private static final Map<String, Object> EMPTY_DATA = new HashMap<String, Object>();

	public abstract SystemScene<?, ?> createScene(Scene scene);

	public final SystemObject load() {
		return load(EMPTY_DATA);
	}

	public abstract SystemObject load(Map<String, Object> data);

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
