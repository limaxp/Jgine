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

public class ScriptScene extends ListSystemScene<ScriptSystem, AbstractScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, AbstractScriptObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, AbstractScriptObject object) {
		object.setEntity(entity);
		object.getInterface().onEnable();
	}

	@Override
	public AbstractScriptObject removeObject(int index) {
		AbstractScriptObject object = super.removeObject(index);
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
	public AbstractScriptObject load(DataInput in) throws IOException {
		String scriptName = in.readUTF();
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		AbstractScriptObject object;
		if (scriptEngine != null)
			object = new ScriptObject((String) scriptName, scriptEngine);
		else
			object = (EntityScript) Script.get(scriptName);
		if (object != null)
			object.load(in);
		return object;
	}

	@Override
	public void save(AbstractScriptObject object, DataOutput out) throws IOException {
		out.writeUTF(object.getName());
		object.save(out);
	}
}
