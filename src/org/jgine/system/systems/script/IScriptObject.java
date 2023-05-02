package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;

public abstract class IScriptObject implements SystemObject {

	protected abstract void setEntity(Entity entity);

	public abstract Entity getEntity();

	public abstract String getName();

	public abstract IScript getInterface();

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;
}
