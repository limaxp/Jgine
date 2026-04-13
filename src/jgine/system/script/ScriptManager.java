package jgine.system.script;

import java.io.Reader;
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

import jgine.utils.Logger;

/**
 * Manager class for scripts.
 */
public class ScriptManager {

	public static final ScriptEngine NULL_SCRIPT_ENGINE = new NullScriptEngine();
	private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();

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

	public static String print() {
		StringBuilder sb = new StringBuilder();
		for (ScriptEngineFactory factory : getFactories()) {
			sb.append(toString(factory));
			sb.append(System.lineSeparator());
		}
		sb.delete(sb.length() - System.lineSeparator().length(), sb.length());
		return sb.toString();
	}

	public static String toString(ScriptEngineFactory factory) {
		StringBuilder sb = new StringBuilder();
		String engineName = factory.getEngineName();
		String engineVersion = factory.getEngineVersion();
		String languageName = factory.getLanguageName();
		String languageVersion = factory.getLanguageVersion();
		sb.append(engineName);
		sb.append('_');
		sb.append(engineVersion);
		sb.append("[Names: ");
		List<String> engNames = factory.getNames();
		sb.append(engNames.get(0));
		for (int i = 1; i < engNames.size(); i++)
			sb.append(", " + engNames.get(i));
		sb.append(" | Language: ");
		sb.append(languageName);
		sb.append('_');
		sb.append(languageVersion);
		sb.append(']');
		return sb.toString();
	}

	public static Object eval(ScriptEngine engine, String text) {
		try {
			return engine.eval(text);
		} catch (ScriptException e) {
			Logger.err("ScriptManager: Error on evaluating [" + text + "]", e);
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
			Logger.err("ScriptManager: Error on evaluating [" + text + "]", e);
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
			Logger.err("ScriptManager: Error on evaluating [" + text + "]", e);
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
			Logger.err("ScriptManager: Error on compiling [" + script + "]", e);
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

	/**
	 * {@link ScriptEngine} implemented after the null object pattern. Also
	 * overrides {@link Invocable} and {@link Compilable}
	 */
	private static class NullScriptEngine implements ScriptEngine, Invocable, Compilable {

		private NullScriptEngine() {
		}

		@Override
		public Object eval(String script, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(String script, Bindings n) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			return null;
		}

		@Override
		public void put(String key, Object value) {
		}

		@Override
		public Object get(String key) {
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			return null;
		}

		@Override
		public void setBindings(Bindings bindings, int scope) {
		}

		@Override
		public Bindings createBindings() {
			return null;
		}

		@Override
		public ScriptContext getContext() {
			return null;
		}

		@Override
		public void setContext(ScriptContext context) {
		}

		@Override
		public ScriptEngineFactory getFactory() {
			return null;
		}

		@Override
		public Object invokeMethod(Object thiz, String name, Object... args)
				throws ScriptException, NoSuchMethodException {
			return null;
		}

		@Override
		public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
			return null;
		}

		@Override
		public <T> T getInterface(Class<T> clasz) {
			return null;
		}

		@Override
		public <T> T getInterface(Object thiz, Class<T> clasz) {
			return null;
		}

		@Override
		public CompiledScript compile(String script) throws ScriptException {
			return null;
		}

		@Override
		public CompiledScript compile(Reader script) throws ScriptException {
			return null;
		}
	}
}
