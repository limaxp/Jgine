package org.jgine.system.systems.script;

import javax.script.ScriptEngine;

import org.jgine.core.entity.Entity;

public abstract class ScriptObjectJava extends ScriptObject implements IScript {

	protected Entity entity;

	public ScriptObjectJava() {
		super(null);
		setName(getType().name);
		scriptInterface = this;
	}

	@Override
	public ScriptObjectJava clone() {
		ScriptObjectJava obj = (ScriptObjectJava) super.clone();
		obj.scriptInterface = obj;
		return obj;
	}

	@Override
	public ScriptEngine getEngine() {
		return null;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	public ScriptType<?> getType() {
		return ScriptTypes.NONE;
	}
}
