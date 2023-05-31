package org.jgine.system.systems.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ListSystemScene;

public class InputScene extends ListSystemScene<InputSystem, InputHandler> {

	public InputScene(InputSystem system, Scene scene) {
		super(system, scene, InputHandler.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, InputHandler object) {
		object.setEntity(entity);
	}

	@Override
	public void update(float dt) {
		for (int i = 0; i < size; i++)
			objects[i].checkInput();
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
		for (int i = 0; i < size; i++)
			objects[i] = InputHandlerTypes.get(in.readInt()).get();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			out.writeInt(objects[i].getType().getId());
	}
}
