package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
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
	@Nullable
	public AiObject removeObject(AiObject object) {
		object.free();
		return super.removeObject(object);
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
