package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;

public abstract class ScriptObjectJava extends ScriptObject implements IScript {

	protected Entity entity;

	public ScriptObjectJava() {
		super(null);
		this.scriptInterface = this;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}
}
