package org.jgine.misc.utils.script;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.jgine.misc.utils.logger.Logger;

public class CompiledScript {

	protected ScriptEngine engine;
	protected Bindings scope;
	protected javax.script.CompiledScript script;

	public CompiledScript() {}

	public CompiledScript(ScriptEngine engine) {
		this.engine = engine;
		ScriptContext context = new SimpleScriptContext();
		context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
		scope = context.getBindings(ScriptContext.ENGINE_SCOPE);
	}

	public javax.script.CompiledScript compile(String script) {
		try {
			return this.script = ((Compilable) engine).compile(script);
		} catch (ScriptException e) {
			Logger.err("Script: Error on compiling text!", e);
			return null;
		}
	}

	public javax.script.CompiledScript compile(Reader reader) {
		try {
			return this.script = ((Compilable) engine).compile(reader);
		} catch (ScriptException e) {
			Logger.err("Script: Error on compiling reader!", e);
			return null;
		}
	}

	public Object eval() {
		try {
			return script.eval(scope);
		} catch (ScriptException e) {
			Logger.err("Script: Error on evaluating text!", e);
			return null;
		}
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public Bindings getScope() {
		return scope;
	}

	public javax.script.CompiledScript getScript() {
		return script;
	}
}
