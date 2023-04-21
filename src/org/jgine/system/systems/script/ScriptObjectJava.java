package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;

public abstract class ScriptObjectJava extends IScriptObject implements IScript, Cloneable {

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
		return getType().name;
	}

	@Override
	public IScript getInterface() {
		return this;
	}

	public ScriptType<?> getType() {
		return ScriptTypes.NONE;
	}
}
