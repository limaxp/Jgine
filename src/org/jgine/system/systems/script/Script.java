package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.entity.Entity;

public abstract class Script extends AbstractScriptObject implements IScript, ScriptBase {

	protected Entity entity;

	public Script() {
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
}
