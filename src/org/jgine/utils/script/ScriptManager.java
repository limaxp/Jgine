package org.jgine.utils.script;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import maxLibs.utils.logger.Logger;

/**
 * Manager class for scripts.
 */
public class ScriptManager {

	public static final ScriptEngine NULL_SCRIPT_ENGINE = new NullScriptEngine();

	private static final ScriptEngineManager ENGINE_MANAGER;

	static {
		ENGINE_MANAGER = new ScriptEngineManager();
	}

	public static void registerFactory(ScriptEngineFactory factory) {
		for (String extenson : factory.getNames())
			ENGINE_MANAGER.registerEngineName(extenson, factory);
		for (String mimeType : factory.getMimeTypes())
			ENGINE_MANAGER.registerEngineMimeType(mimeType, factory);
		for (String extenson : factory.getExtensions())
			ENGINE_MANAGER.registerEngineExtension(extenson, factory);
	}

	public static ScriptEngine getEngineByName(String name) {
		return ENGINE_MANAGER.getEngineByName(name);
	}

	public static ScriptEngine getEngineByMimeType(String mimeType) {
		return ENGINE_MANAGER.getEngineByMimeType(mimeType);
	}

	public static ScriptEngine getEngineByExtension(String extension) {
		return ENGINE_MANAGER.getEngineByExtension(extension);
	}

	public static void setProperty(String name, Object value) {
		ENGINE_MANAGER.put(name, value);
	}

	public static Object getProperty(String name) {
		return ENGINE_MANAGER.get(name);
	}

	public static void setBindings(Bindings bindings) {
		ENGINE_MANAGER.setBindings(bindings);
	}

	public static Bindings getBindings() {
		return ENGINE_MANAGER.getBindings();
	}

	public static List<ScriptEngineFactory> getFactories() {
		return ENGINE_MANAGER.getEngineFactories();
	}

	public static List<String> getFactoryInfo() {
		List<String> result = new ArrayList<String>();
		for (ScriptEngineFactory factory : getFactories())
			result.add(getFactoryInfo(factory));
		return result;
	}

	public static String getFactoryInfo(ScriptEngineFactory factory) {
		StringBuilder stringBuilder = new StringBuilder();
		String engName = factory.getEngineName();
		String engVersion = factory.getEngineVersion();
		String langName = factory.getLanguageName();
		String langVersion = factory.getLanguageVersion();
		stringBuilder.append(engName);
		stringBuilder.append(' ');
		stringBuilder.append('(');
		stringBuilder.append(engVersion);
		stringBuilder.append(')');
		List<String> engNames = factory.getNames();
		stringBuilder.append(' ');
		stringBuilder.append('[');
		stringBuilder.append(engNames.get(0));
		for (int i = 1; i < engNames.size(); i++)
			stringBuilder.append(", " + engNames.get(i));
		stringBuilder.append(']');
		stringBuilder.append(" Language: ");
		stringBuilder.append(langName);
		stringBuilder.append(' ');
		stringBuilder.append('(');
		stringBuilder.append(langVersion);
		stringBuilder.append(')');
		return stringBuilder.toString();
	}

	public static Object eval(ScriptEngine engine, String text) {
		try {
			return engine.eval(text);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating text!", e);
			return null;
		}
	}

	public static Object eval(ScriptEngine engine, Reader reader) {
		try {
			return engine.eval(reader);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating reader!", e);
			return null;
		}
	}

	public static Object eval(ScriptEngine engine, Bindings bindings, String text) {
		try {
			return engine.eval(text, bindings);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating text!", e);
			return null;
		}
	}

	public static Object eval(ScriptEngine engine, Bindings bindings, Reader reader) {
		try {
			return engine.eval(reader, bindings);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating reader!", e);
			return null;
		}
	}

	public static Object eval(ScriptEngine engine, ScriptContext context, String text) {
		try {
			return engine.eval(text, context);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating text!", e);
			return null;
		}
	}

	public static Object eval(ScriptEngine engine, ScriptContext context, Reader reader) {
		try {
			return engine.eval(reader, context);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating reader!", e);
			return null;
		}
	}

	public static <T> T getInterface(ScriptEngine engine, Class<T> clazz) {
		return ((Invocable) engine).getInterface(clazz);
	}

	public static <T> T getInterface(ScriptEngine engine, Object obj, Class<T> clazz) {
		return ((Invocable) engine).getInterface(obj, clazz);
	}

	public static Object invoke(ScriptEngine engine, String name, Object... objects) {
		try {
			return ((Invocable) engine).invokeFunction(name, objects);
		} catch (ScriptException | NoSuchMethodException e) {
			Logger.err("ScriptManager: Error on invoking script function'" + name + "'", e);
			return null;
		}
	}

	public static Object invoke(ScriptEngine engine, Object obj, String name, Object... objects) {
		try {
			return ((Invocable) engine).invokeMethod(obj, name, objects);
		} catch (ScriptException | NoSuchMethodException e) {
			Logger.err("ScriptManager: Error on invoking script function'" + name + "'", e);
			return null;
		}
	}

	public static final CompiledScript compile(ScriptEngine engine, String script) {
		try {
			return ((Compilable) engine).compile(script);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on compiling text!", e);
			return null;
		}
	}

	public static final CompiledScript compile(ScriptEngine engine, Reader reader) {
		try {
			return ((Compilable) engine).compile(reader);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on compiling reader!", e);
			return null;
		}
	}

	public static Object run(CompiledScript script) {
		return run(script, script.getEngine().getContext());
	}

	public static Object run(CompiledScript script, ScriptContext context) {
		try {
			return script.eval(context);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on running script!", e);
			return null;
		}
	}

	public static Object run(CompiledScript script, Bindings bindings) {
		try {
			return script.eval(bindings);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on running script!", e);
			return null;
		}
	}
}
