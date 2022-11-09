package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;

public abstract class ScriptJava implements IScript {

	protected Entity entity;

	@Override
	public Entity getEntity() {
		return entity;
	}
}
