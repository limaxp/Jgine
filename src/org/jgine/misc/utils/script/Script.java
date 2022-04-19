package org.jgine.misc.utils.script;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.jgine.misc.utils.logger.Logger;

public class Script {

	protected ScriptEngine engine;
	protected Bindings scope;

	public Script() {}

	public Script(ScriptEngine engine) {
		this.engine = engine;
		ScriptContext context = new SimpleScriptContext();
		context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
		scope = context.getBindings(ScriptContext.ENGINE_SCOPE);
		engine.setBindings(scope, ScriptContext.ENGINE_SCOPE);
	}

	public void eval(String text) {
		try {
			engine.eval(text, scope);
		} catch (ScriptException e) {
			Logger.err("Script: Error on evaluating text!", e);
		}
	}

	public void eval(Reader reader) {
		try {
			engine.eval(reader, scope);
		} catch (ScriptException e) {
			Logger.err("Script: Error on evaluating reader!", e);
		}
	}

	public void put(String name, Object obj) {
		scope.put(name, obj);
	}

	public Object get(String name) {
		return scope.get(name);
	}

	public void invoke(String name, Object... objects) {
		try {
			((Invocable) engine).invokeFunction(name, objects);
		} catch (ScriptException | NoSuchMethodException e) {
			Logger.err("ScriptManager: Error on invoking script function'" + name + "'", e);
		}
	}

	public void invoke(Object obj, String name, Object... objects) {
		try {
			((Invocable) engine).invokeMethod(obj, name, objects);
		} catch (ScriptException | NoSuchMethodException e) {
			Logger.err("ScriptManager: Error on invoking script function'" + name + "'", e);
		}
	}

	public <T> T getInterface(Class<T> clazz) {
		return ((Invocable) engine).getInterface(clazz);
	}

	public <T> T getInterface(Object obj, Class<T> clazz) {
		return ((Invocable) engine).getInterface(obj, clazz);
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public Bindings getScope() {
		return scope;
	}
}
