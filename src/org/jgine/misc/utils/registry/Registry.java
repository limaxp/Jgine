package org.jgine.misc.utils.registry;

import java.util.Collection;
import java.util.Iterator;

import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.ui.UIObjectType;

public abstract class Registry<T> {

	public static final Registry<ColliderType<?>> COLLIDER_TYPES = new GenIdRegistry<ColliderType<?>>("collider");

	public static final Registry<InputHandlerType<?>> INPUT_HANDLER_TYPES = new GenIdRegistry<InputHandlerType<?>>(
			"input_handler");

	public static final Registry<UIObjectType<?>> UI_OBJECTS_TYPES = new GenIdRegistry<UIObjectType<?>>("ui_objects");

	public final String name;

	public Registry(String name) {
		this.name = name;
	}

	public abstract int register(String key, T value);

	public abstract boolean register(int id, String key, T value);

	public abstract T get(String key);

	public abstract T getOrDefault(String key, T defaultValue);

	public abstract T get(int id);

	public abstract T getOrDefault(int id, T defaultValue);

	public abstract T getByIndex(int index);

	public abstract Collection<T> values();

	public abstract Iterator<T> iterator();

	public abstract int size();
}
