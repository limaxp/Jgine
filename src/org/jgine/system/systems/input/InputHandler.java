package org.jgine.system.systems.input;

import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.system.SystemObject;

public abstract class InputHandler implements SystemObject {

	private Entity entity;
	protected byte inputSlot = Input.Slot.KEYBOARD;

	public abstract void checkInput();

	public abstract InputHandlerType<?> getType();

	protected void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setInputSlot(byte inputSlot) {
		this.inputSlot = inputSlot;
	}

	public byte getInputSlot() {
		return inputSlot;
	}
}
