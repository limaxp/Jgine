package org.jgine.system.systems.input;

import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.device.Gamepad;
import org.jgine.system.SystemObject;

public abstract class InputHandler implements SystemObject {

	private Entity entity;
	protected InputDevice[] inputDevices;

	protected InputHandler() {
		Gamepad gamepad = Input.getGamepad(Gamepad.Slot.GAMEPAD_1);
		if (gamepad != null)
			inputDevices = new InputDevice[] { Input.getMouse(), Input.getKeyboard(), gamepad };
		else
			inputDevices = new InputDevice[] { Input.getMouse(), Input.getKeyboard() };
	}

	public final void checkInput() {
		for (InputDevice inputDevice : inputDevices)
			checkInput(inputDevice);
	}

	public abstract void checkInput(InputDevice inputDevice);

	public abstract InputHandlerType<?> getType();

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setInputDevice(InputDevice[] inputDevices) {
		this.inputDevices = inputDevices;
	}

	public InputDevice[] getInputDevices() {
		return inputDevices;
	}
}
