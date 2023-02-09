package org.jgine.system.systems.script;

import javax.script.ScriptEngine;

import org.jgine.system.SystemObject;

public class ScriptObject implements SystemObject, Cloneable {

	private String name;
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

	void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public IScript getInterface() {
		return scriptInterface;
	}
}
