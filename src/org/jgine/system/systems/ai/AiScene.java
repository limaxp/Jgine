package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ObjectSystemScene;

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
	public void onRemove(Entity entity, AiObject object) {
		object.free();
	}

	@Override
	public void update(UpdateTask update) {
		forEach((o) -> o.update(update.dt));
		update.finish(system);
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).entity;
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
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
