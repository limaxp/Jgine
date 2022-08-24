package org.jgine.system.systems.script;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptEngine;

public class ScriptObjectJava extends ScriptObject {

	public ScriptObjectJava(IScript script) {
		this.script = script;
	}

	public void eval(String text) {
	}

	public void eval(Reader reader) {
	}

	public void put(String name, Object obj) {
	}

	public Object get(String name) {
		return null;
	}

	public void invoke(String name, Object... objects) {
	}

	public void invoke(Object obj, String name, Object... objects) {
	}

	public <T> T getInterface(Class<T> clazz) {
		return null;
	}

	public <T> T getInterface(Object obj, Class<T> clazz) {
		return null;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public Bindings getScope() {
		return scope;
	}
}
