package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.script.ScriptEngine;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.system.data.ListSystemScene;

public class ScriptScene extends ListSystemScene<ScriptSystem, IScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, IScriptObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, IScriptObject object) {
		object.setEntity(entity);
		object.getInterface().onEnable();
	}

	@Override
	public IScriptObject removeObject(int index) {
		IScriptObject object = super.removeObject(index);
		object.getInterface().onDisable();
		return object;
	}

	@Override
	public void update(float dt) {
		for (int i = 0; i < size; i++)
			objects[i].getInterface().update();
	}

	@Override
	public void render() {
	}

	@Override
	public Entity getEntity(int index) {
		return objects[index].getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	public IScriptObject load(DataInput in) throws IOException {
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
	public void save(IScriptObject object, DataOutput out) throws IOException {
		out.writeUTF(object.getName());
	}
}
