package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.script.ScriptEngine;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;
import org.jgine.utils.script.ScriptManager;

public class ScriptObject extends IScriptObject implements Cloneable {

	public final String name;
	protected ScriptEngine engine;
	protected IScript scriptInterface;

	public ScriptObject(ScriptEngine engine) {
		this(null, engine);
	}

	public ScriptObject(String name, ScriptEngine engine) {
		this.name = name;
		this.engine = engine;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public ScriptObject clone() {
		try {
			return (ScriptObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void setEntity(Entity entity) {
		scriptInterface = (IScript) ScriptManager.invoke(engine, "create", entity);
	}

	@Override
	public Entity getEntity() {
		return scriptInterface.getEntity();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IScript getInterface() {
		return scriptInterface;
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}
}
