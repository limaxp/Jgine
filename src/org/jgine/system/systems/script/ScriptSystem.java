package org.jgine.system.systems.script;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.utils.Reflection;
import org.jgine.misc.utils.script.EventManager;
import org.jgine.misc.utils.script.ScriptManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class ScriptSystem extends EngineSystem {

	public ScriptSystem() {
		EventManager.setScriptSupplier((entity) -> entity.getSystems(this));
	}

	@Override
	public SystemScene<?, ?> createScene(Scene scene) {
		return new ScriptScene(this, scene);
	}

	@Override
	public ScriptObject load(Map<String, Object> data) {
		// TODO this uses the same bindings for every Entity with same Script!

		// TODO use different ScriptEngines for different file extensions!
		ScriptObject script = new ScriptObject(ScriptManager.getEngineByName("bsh"));
		Object scriptName = data.get("script");
		if (scriptName != null && scriptName instanceof String) {
			String scriptString = ResourceManager.getScript((String) scriptName);
			if (scriptString != null)
				script.eval(scriptString);
		}

		Object interfaceName = data.get("interface");
		if (interfaceName != null && interfaceName instanceof String) {
			@SuppressWarnings("unchecked")
			Class<IScript> interfaceClass = (Class<IScript>) Reflection.getClass((String) interfaceName);
			if (interfaceClass != null)
				script.script = script.getInterface(interfaceClass);
			else
				script.script = script.getInterface(IScript.class);
		} else
			script.script = script.getInterface(IScript.class);
		return script;
	}
}
