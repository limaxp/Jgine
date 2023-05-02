package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;
import org.jgine.utils.Reflection;
import org.reflections.Reflections;

public abstract class ScriptObjectJava extends IScriptObject implements IScript, Cloneable {

	private static final Map<String, Supplier<ScriptObjectJava>> MAP = new HashMap<String, Supplier<ScriptObjectJava>>();

	public static void register(Package pkg) {
		String name = pkg.getName();
		Reflections reflections = new Reflections(name.substring(0, name.indexOf('.')));
		for (Class<?> c : reflections.getSubTypesOf(ScriptObjectJava.class)) {
			MAP.put(c.getSimpleName(), () -> (ScriptObjectJava) Reflection.newInstance(c));
		}
	}

	protected Entity entity;

	public ScriptObjectJava() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public ScriptObjectJava clone() {
		try {
			return (ScriptObjectJava) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public IScript getInterface() {
		return this;
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}

	@Nullable
	public static ScriptObjectJava get(String name) {
		return MAP.getOrDefault(name, () -> null).get();
	}
}
