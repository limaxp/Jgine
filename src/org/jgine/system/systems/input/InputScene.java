package org.jgine.system.systems.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ObjectSystemScene;

public class InputScene extends ObjectSystemScene<InputSystem, InputHandler> {

	public InputScene(InputSystem system, Scene scene) {
		super(system, scene, InputHandler.class, 10000);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, InputHandler object) {
		object.setEntity(entity);
	}

	@Override
	public void update(UpdateTask update) {
		for (int i = 0; i < size; i++)
			objects[i].checkInput();
		update.finish(system);
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
		for (int i = 0; i < size; i++) {
			InputHandler object = InputHandler.get(in.readUTF());
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			InputHandler object = objects[i];
			out.writeUTF(object.getClass().getSimpleName());
			object.save(out);
		}
	}
}
