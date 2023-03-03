package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ListSystemScene;

public class AiScene extends ListSystemScene<AiSystem, AiObject> {

	public AiScene(AiSystem system, Scene scene) {
		super(system, scene, AiObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, AiObject object) {
		object.init(entity);
	}

	@Override
	public AiObject removeObject(int index) {
		AiObject object = super.removeObject(index);
		object.free();
		return object;
	}

	@Override
	public void update(float dt) {
		for (int i = 0; i < size; i++)
			objects[i].update(dt);
	}

	@Override
	public void render() {
	}

	@Override
	public Entity getEntity(int index) {
		return objects[index].entity;
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	public AiObject load(DataInput in) throws IOException {
		AiObject object = new AiObject();
		object.load(in);
		return object;
	}

	@Override
	public void save(AiObject object, DataOutput out) throws IOException {
		object.save(out);
	}
}
