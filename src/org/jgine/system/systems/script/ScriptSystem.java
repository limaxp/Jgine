package org.jgine.system.systems.script;

import java.util.Map;

import javax.script.ScriptEngine;

import org.jgine.core.Scene;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.utils.script.EventManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class ScriptSystem extends EngineSystem {

	public ScriptSystem() {
		super("script");
		EventManager.setScriptSupplier((entity) -> entity.getSystems(this));
	}

	@Override
	public SystemScene<?, ?> createScene(Scene scene) {
		return new ScriptScene(this, scene);
	}

	@Override
	public ScriptObject load(Map<String, Object> data) {
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
}
