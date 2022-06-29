package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.input.InputHandlerTypes;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.physic.PhysicSystem;
import org.jgine.system.systems.transform.Transform;
import org.jgine.system.systems.transform.TransformSystem;

public class TransformInputHandler extends InputHandler {

	private Transform transform;
	private PhysicObject physicObject;

	private double lastMouseX;
	private double lastMouseY;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		transform = entity.getSystem(TransformSystem.class);
		physicObject = entity.getSystem(PhysicSystem.class);
	}

	@Override
	public void checkInput(InputDevice inputDevice) {
		if (!inputDevice.isKeyboard())
			return;

		Vector2f cursorPos = Input.getCursorPos();
		if (cursorPos.x > lastMouseX)
			transform.rotateY(0.015f);
		else if (cursorPos.x < lastMouseX)
			transform.rotateY(-0.015f);

		if (cursorPos.y > lastMouseY)
			transform.rotateX(0.015f);
		else if (cursorPos.y < lastMouseY)
			transform.rotateX(-0.015f);
		lastMouseX = cursorPos.x;
		lastMouseY = cursorPos.y;

		if (inputDevice.isKeyPressed(Key.KEY_ESCAPE))
			Engine.getInstance().shutdown();
		if (inputDevice.isKeyPressed(Key.KEY_F12))
			Scheduler.runTaskSynchron(() -> Engine.getInstance().getWindow().toggleFullScreen());

		if (inputDevice.isKeyPressed(Key.KEY_W) || inputDevice.isKeyPressed(Key.KEY_UP))
			physicObject.addVelocity(Vector3f.mult(transform.getLocalRotation(), 0.1f));
		if (inputDevice.isKeyPressed(Key.KEY_S) || inputDevice.isKeyPressed(Key.KEY_DOWN))
			physicObject.addVelocity(Vector3f.mult(transform.getLocalRotation(), -0.1f));
		if (inputDevice.isKeyPressed(Key.KEY_A) || inputDevice.isKeyPressed(Key.KEY_LEFT))
			physicObject.addVelocity(Vector3f.mult(Vector3f.getLeft(transform.getLocalRotation()), 0.1f));
		if (inputDevice.isKeyPressed(Key.KEY_D) || inputDevice.isKeyPressed(Key.KEY_RIGHT))
			physicObject.addVelocity(Vector3f.mult(Vector3f.getRight(transform.getLocalRotation()), 0.1f));
		if (inputDevice.isKeyPressed(Key.KEY_SPACE))
			physicObject.addVelocity(Vector3f.mult(Vector3f.UP, 0.1f));
		if (inputDevice.isKeyPressed(Key.KEY_LSHIFT))
			physicObject.addVelocity(Vector3f.mult(Vector3f.DOWN, 0.1f));
	}

	@Override
	public InputHandlerType<TransformInputHandler> getType() {
		return InputHandlerTypes.TRANSFORM;
	}
}
