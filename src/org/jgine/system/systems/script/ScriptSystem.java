package org.jgine.system.systems.script;

import java.util.Map;
import java.util.function.BiConsumer;

import javax.script.ScriptEngine;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.function.TriConsumer;

public class ScriptSystem extends EngineSystem {

	public ScriptSystem() {
		super("script");
	}

	@Override
	public SystemScene<?, ?> createScene(Scene scene) {
		return new ScriptScene(this, scene);
	}

	@Override
	public IScriptObject load(Map<String, Object> data) {
		Object scriptName = data.get("script");
		if (!(scriptName instanceof String))
			return null;
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		if (scriptEngine != null)
			return new ScriptObject((String) scriptName, scriptEngine);
		ScriptType<?> type = ScriptTypes.get((String) scriptName);
		if (type != null)
			return type.get();
		return null;
	}

	public static <T> void callEvent(Entity entity, T t, BiConsumer<IScript, T> func) {
		SystemObject[] scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.length; i++)
				func.accept(((IScriptObject) scripts[i]).getInterface(), t);
	}

	public static <T1, T2> void callEvent(Entity entity, T1 t1, T2 t2, TriConsumer<IScript, T1, T2> func) {
		SystemObject[] scripts = entity.getSystems(Engine.SCRIPT_SYSTEM);
		if (scripts != null)
			for (int i = 0; i < scripts.length; i++)
				func.accept(((IScriptObject) scripts[i]).getInterface(), t1, t2);
	}
}
