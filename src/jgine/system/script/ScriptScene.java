package jgine.system.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.core.Engine.UpdateTask;
import jgine.system.ObjectSystemScene.EntitySystemScene;
import jgine.utils.loader.ResourceManager;

public class ScriptScene extends EntitySystemScene<ScriptSystem, Script> {

	protected List<Updateable> updateAbles;

	public ScriptScene(ScriptSystem system, Scene scene) {
		super(system, scene, Script.class, 10000);
		updateAbles = new ArrayList<Updateable>(10000);
	}

	@Override
	public void free() {
		forEach((o) -> o.onDisable());
	}

	@Override
	public void onInit(Entity entity, Script object) {
		object.onEnable(entity);
	}

	@Override
	protected void onAdd(Entity entity, Script object) {
		if (object instanceof Updateable u)
			updateAbles.add(u);
	}

	@Override
	protected void onRemove(Entity entity, Script object) {
		object.onDisable();
		if (object instanceof Updateable u)
			updateAbles.remove(u);
	}

	@Override
	public void update(UpdateTask update) {
		int size = updateAbles.size();
		for (int i = 0; i < size; i++)
			updateAbles.get(i).update();
		update.finish(id);
	}

	@Override
	protected void saveData(Script object, DataOutput out) throws IOException {
		out.writeUTF(object.getName());
		object.save(out);
	}

	@Override
	protected Script loadData(DataInput in) throws IOException {
		String scriptName = in.readUTF();
		ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
		Script object;
		if (scriptEngine != null)
			object = new ScriptObject((String) scriptName, scriptEngine);
		else
			object = (Script) ScriptBase.get(scriptName);
		object.load(in);
		return object;
	}
}
