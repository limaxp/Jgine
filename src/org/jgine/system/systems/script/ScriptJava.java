package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;

public abstract class ScriptJava implements IScript {

	protected Entity entity;

	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}
}
