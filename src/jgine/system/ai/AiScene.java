package jgine.system.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.core.Engine.UpdateTask;
import jgine.system.ObjectSystemScene;
import jgine.system.transform.Transform;

public class AiScene extends ObjectSystemScene<AiSystem, AiObject> {

	public AiScene(AiSystem system, Scene scene) {
		super(system, scene, AiObject.class, 10000);
	}

	@Override
	public void free() {
		forEach(AiObject::free);
	}

	@Override
	public void onInit(Entity entity, AiObject object) {
		object.init(entity);
	}

	@Override
	protected void onRemove(Entity entity, AiObject object) {
		object.free();
	}

	@Override
	public void update(UpdateTask update) {
		forEach((o) -> o.update(update.dt));
		update.finish(id);
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).entity;
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).getTransform();
	}

	@Override
	protected void saveData(AiObject object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected AiObject loadData(DataInput in) throws IOException {
		AiObject object = new AiObject();
		object.load(in);
		return object;
	}
}
