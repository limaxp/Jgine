package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.system.data.ListSystemScene;
import org.jgine.utils.script.ScriptManager;

public class ScriptScene extends ListSystemScene<ScriptSystem, ScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, ScriptObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, ScriptObject object) {
		if (object instanceof ScriptObjectJava)
			((ScriptObjectJava) object).entity = entity;
		else
			object.scriptInterface = (IScript) ScriptManager.invoke(object.engine, "create", entity);
		object.scriptInterface.onEnable();
	}

	@Override
	@Nullable
	public ScriptObject removeObject(ScriptObject object) {
		object.scriptInterface.onDisable();
		return super.removeObject(object);
	}

	@Override
	public void update(float dt) {
		for (int i = 0; i < size; i++)
			objects[i].scriptInterface.update();
	}

	@Override
	public void render() {
	}

	@Override
	public ScriptObject load(DataInput in) throws IOException {
		String scriptName = in.readUTF();
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		if (scriptEngine != null)
			return new ScriptObject((String) scriptName, scriptEngine);
		ScriptType<?> type = ScriptTypes.get((String) scriptName);
		if (type != null)
			return type.get();
		return null;
	}

	@Override
	public void save(ScriptObject object, DataOutput out) throws IOException {
		out.writeUTF(object.getName());
	}
}
