package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;

public abstract class AbstractScriptObject implements SystemObject, Cloneable {

	protected abstract void setEntity(Entity entity);

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public AbstractScriptObject clone() {
		try {
			return (AbstractScriptObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public abstract Entity getEntity();

	public abstract String getName();

	public abstract IScript getInterface();

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;
}
