package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.input.InputHandlerTypes;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.physic.PhysicSystem;

public class TransformInputHandler2D extends InputHandler {

	private PhysicObject physicObject;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		physicObject = entity.getSystem(PhysicSystem.class);
	}

	@Override
	public void checkInput() {
		if (Input.isKeyPressed(Key.KEY_ESCAPE))
			Engine.getInstance().shutdown();
		if (Input.isKeyPressed(Key.KEY_F12))
			Scheduler.runTaskSynchron(() -> Engine.getInstance().getWindow().toggleFullScreen());

		if (Input.isKeyPressed(Key.KEY_W) || Input.isKeyPressed(Key.KEY_UP))
			physicObject.addVelocity(Vector2f.mult(Vector2f.UP, 0.1f));
		if (Input.isKeyPressed(Key.KEY_S) || Input.isKeyPressed(Key.KEY_DOWN))
			physicObject.addVelocity(Vector2f.mult(Vector2f.DOWN, 0.1f));
		if (Input.isKeyPressed(Key.KEY_A) || Input.isKeyPressed(Key.KEY_LEFT))
			physicObject.addVelocity(Vector2f.mult(Vector2f.LEFT, 0.1f));
		if (Input.isKeyPressed(Key.KEY_D) || Input.isKeyPressed(Key.KEY_RIGHT))
			physicObject.addVelocity(Vector2f.mult(Vector2f.RIGHT, 0.1f));
	}

	@Override
	public InputHandlerType<TransformInputHandler2D> getType() {
		return InputHandlerTypes.TRANSFORM_2D;
	}
}
