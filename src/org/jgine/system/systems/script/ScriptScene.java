package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.script.ScriptEngine;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ListSystemScene;
import org.jgine.utils.loader.ResourceManager;

public class ScriptScene extends ListSystemScene<ScriptSystem, AbstractScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, AbstractScriptObject.class);
	}

	@Override
	public void free() {
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				objects[i].getInterface().onDisable();
		}
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
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				objects[i].getInterface().update();
		}
	}

	@Override
	public void render(float dt) {
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
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			String scriptName = in.readUTF();
			ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
			AbstractScriptObject object;
			if (scriptEngine != null)
				object = new ScriptObject((String) scriptName, scriptEngine);
			else
				object = (EntityScript) Script.get(scriptName);
			if (object != null) {
				object.load(in);
				objects[i] = object;
			}
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			AbstractScriptObject object = objects[i];
			out.writeUTF(object.getName());
			object.save(out);
		}
	}
}
