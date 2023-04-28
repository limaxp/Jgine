package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;

public abstract class IScriptObject implements SystemObject {

	protected abstract void setEntity(Entity entity);

	public abstract Entity getEntity();

	public abstract String getName();

	public abstract IScript getInterface();
}
