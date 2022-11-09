package org.jgine.system.systems.script;

import javax.script.ScriptEngine;

import org.jgine.system.SystemObject;

public class ScriptObject implements SystemObject, Cloneable {

	protected ScriptEngine engine;
	protected IScript scriptInterface;

	public ScriptObject(ScriptEngine engine) {
		this.engine = engine;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public IScript getInterface() {
		return scriptInterface;
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
}
