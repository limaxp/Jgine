package jgine.system.script;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.script.ScriptEngine;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.core.Scene;
import jgine.system.EngineSystem;
import jgine.utils.collection.function.QuadConsumer;
import jgine.utils.collection.function.QuintConsumer;
import jgine.utils.collection.function.TriConsumer;
import jgine.utils.loader.ResourceManager;

public class ScriptSystem extends EngineSystem<ScriptSystem, Script> {

	public ScriptSystem() {
		super("script");
	}

	@Override
	public ScriptScene createScene(Scene scene) {
		return new ScriptScene(this, scene);
	}

	@Override
	public Script load(Map<String, Object> data) {
		Object scriptName = data.get("type");
		if (!(scriptName instanceof String))
			return null;
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		if (scriptEngine != null)
			return new ScriptObject((String) scriptName, scriptEngine);
		return (Script) ScriptBase.get((String) scriptName);
	}

	public static void callEvent(Entity entity, Consumer<IScript> func) {
		entity.forEach(Engine.SCRIPT, func::accept);
	}

	public static <T> void callEvent(Entity entity, T t, BiConsumer<IScript, T> func) {
		entity.forEach(Engine.SCRIPT, (Script script) -> func.accept(script, t));
	}

	public static <T1, T2> void callEvent(Entity entity, T1 t1, T2 t2, TriConsumer<IScript, T1, T2> func) {
		entity.forEach(Engine.SCRIPT, (Script script) -> func.accept(script, t1, t2));
	}

	public static <T1, T2, T3> void callEvent(Entity entity, T1 t1, T2 t2, T3 t3,
			QuadConsumer<IScript, T1, T2, T3> func) {
		entity.forEach(Engine.SCRIPT, (Script script) -> func.accept(script, t1, t2, t3));
	}

	public static <T1, T2, T3, T4> void callEvent(Entity entity, T1 t1, T2 t2, T3 t3, T4 t4,
			QuintConsumer<IScript, T1, T2, T3, T4> func) {
		entity.forEach(Engine.SCRIPT, (Script script) -> func.accept(script, t1, t2, t3, t4));
	}
}
