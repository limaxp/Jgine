package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.script.ScriptEngine;

import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ObjectSystemScene;
import org.jgine.utils.loader.ResourceManager;

public class ScriptScene extends ObjectSystemScene<ScriptSystem, AbstractScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, AbstractScriptObject.class, 10000);
	}

	@Override
	public void free() {
		forEach((o) -> o.getInterface().onDisable());
	}

	@Override
	public void onInit(Entity entity, AbstractScriptObject object) {
		object.setEntity(entity);
		object.getInterface().onEnable();
	}

	@Override
	public void onRemove(Entity entity, AbstractScriptObject object) {
		object.getInterface().onDisable();
	}

	@Override
	public void update(UpdateTask update) {
		forEach((o) -> o.getInterface().update());
		update.finish(system);
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	protected void saveData(AbstractScriptObject object, DataOutput out) throws IOException {
		out.writeUTF(object.getName());
		object.save(out);
	}

	@Override
	protected AbstractScriptObject loadData(DataInput in) throws IOException {
		String scriptName = in.readUTF();
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		AbstractScriptObject object;
		if (scriptEngine != null)
			object = new ScriptObject((String) scriptName, scriptEngine);
		else
			object = (EntityScript) Script.get(scriptName);
		object.load(in);
		return object;
	}
}
