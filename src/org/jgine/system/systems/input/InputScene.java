package org.jgine.system.systems.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
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
	public void init(Entity entity, InputHandler object) {
		object.setEntity(entity);
	}

	@Override
	public void update(UpdateTask update) {
		forEach(InputHandler::checkInput);
		update.finish(system);
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	protected void saveData(InputHandler object, DataOutput out) throws IOException {
		out.writeUTF(object.getClass().getSimpleName());
		object.save(out);
	}

	@Override
	protected InputHandler loadData(DataInput in) throws IOException {
		InputHandler object = InputHandler.get(in.readUTF());
		object.load(in);
		return object;
	}
}
