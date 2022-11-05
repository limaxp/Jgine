package org.jgine.system.systems.input;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
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
	public void update() {
		for (int i = 0; i < size; i++)
			objects[i].checkInput();
	}

	@Override
	public void render() {
	}

	@Override
	public InputHandler load(DataInput in) throws IOException {
		InputHandler object = InputHandlerTypes.get(in.readInt()).get();
		return object;
	}

	@Override
	public void save(InputHandler object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
	}
}
