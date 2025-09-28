package org.jgine.system.systems.script;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.script.ScriptEngine;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.utils.collection.function.QuadConsumer;
import org.jgine.utils.collection.function.QuintConsumer;
import org.jgine.utils.collection.function.TriConsumer;
import org.jgine.utils.loader.ResourceManager;

public class ScriptSystem extends EngineSystem<ScriptSystem, AbstractScriptObject> {

	public ScriptSystem() {
		super("script");
	}

	@Override
	public ScriptScene createScene(Scene scene) {
		return new ScriptScene(this, scene);
	}

	@Override
	public AbstractScriptObject load(Map<String, Object> data) {
		Object scriptName = data.get("type");
		if (!(scriptName instanceof String))
			return null;
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		if (scriptEngine != null)
			return new ScriptObject((String) scriptName, scriptEngine);
		return (Script) ScriptBase.get((String) scriptName);
	}

	public static void callEvent(Entity entity, Consumer<IScript> func) {
		List<AbstractScriptObject> scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.size(); i++)
				func.accept(scripts.get(i).getInterface());
	}

	public static <T> void callEvent(Entity entity, T t, BiConsumer<IScript, T> func) {
		List<AbstractScriptObject> scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.size(); i++)
				func.accept(scripts.get(i).getInterface(), t);
	}

	public static <T1, T2> void callEvent(Entity entity, T1 t1, T2 t2, TriConsumer<IScript, T1, T2> func) {
		List<AbstractScriptObject> scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.size(); i++)
				func.accept(scripts.get(i).getInterface(), t1, t2);
	}

	public static <T1, T2, T3> void callEvent(Entity entity, T1 t1, T2 t2, T3 t3,
			QuadConsumer<IScript, T1, T2, T3> func) {
		List<AbstractScriptObject> scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.size(); i++)
				func.accept(scripts.get(i).getInterface(), t1, t2, t3);
	}

	public static <T1, T2, T3, T4> void callEvent(Entity entity, T1 t1, T2 t2, T3 t3, T4 t4,
			QuintConsumer<IScript, T1, T2, T3, T4> func) {
		List<AbstractScriptObject> scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.size(); i++)
				func.accept(scripts.get(i).getInterface(), t1, t2, t3, t4);
	}
}
