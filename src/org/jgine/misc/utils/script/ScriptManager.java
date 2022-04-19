package org.jgine.misc.utils.script;

import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import bsh.BshScriptEngineFactory;

public class ScriptManager {

	private static final ScriptEngineManager ENGINE_MANAGER;

	static {
		ENGINE_MANAGER = new ScriptEngineManager();
		registerFactory(new BshScriptEngineFactory());
	}

	public static void registerFactory(ScriptEngineFactory factory) {
		// TODO add to ENGINE_MANAGER.getEngineFactories() somehow!
		for (String extenson : factory.getNames())
			ENGINE_MANAGER.registerEngineName(extenson, factory);
		for (String mimeType : factory.getMimeTypes())
			ENGINE_MANAGER.registerEngineMimeType(mimeType, factory);
		for (String extenson : factory.getExtensions())
			ENGINE_MANAGER.registerEngineExtension(extenson, factory);
	}

	public static List<ScriptEngineFactory> getFactories() {
		return ENGINE_MANAGER.getEngineFactories();
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

	public static void setGlobal(String name, Object value) {
		ENGINE_MANAGER.put(name, value);
	}

	public static Object getGlobal(String name) {
		return ENGINE_MANAGER.get(name);
	}

	public static void setGlobalBindings(Bindings bindings) {
		ENGINE_MANAGER.setBindings(bindings);
	}

	public static Object getGlobalBindings() {
		return ENGINE_MANAGER.getBindings();
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
}
