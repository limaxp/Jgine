package org.jgine.system.systems.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.TaskManager;
import org.jgine.system.data.ListSystemScene;

public class ScriptScene extends ListSystemScene<ScriptSystem, ScriptObject> {

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, ScriptObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, ScriptObject object) {
		object.put("entity", entity);
		object.script.setEntity(entity);
		object.script.onEnable();
	}

	@Override
	@Nullable
	public ScriptObject removeObject(ScriptObject object) {
		object.script.onDisable();
		return super.removeObject(object);
	}

	@Override
	public void update() {
		TaskManager.execute(size, this::update);
	}

	public void update(int index, int size) {
		size = index + size;
		for (; index < size; index++)
			objects[index].script.update();
	}

	@Override
	public void render() {
	}

	@Override
	public ScriptObject load(DataInput in) throws IOException {
		return null;
	}

	@Override
	public void save(ScriptObject object, DataOutput out) throws IOException {

	}
}
