package org.jgine.system.systems.script;

import javax.script.ScriptEngine;

import org.jgine.misc.utils.script.Script;
import org.jgine.system.SystemObject;

public class ScriptObject extends Script implements SystemObject {

	protected IScript script;

	public ScriptObject() {
	}

	public ScriptObject(ScriptEngine engine) {
		super(engine);
	}

	public IScript getScript() {
		return script;
	}
}
