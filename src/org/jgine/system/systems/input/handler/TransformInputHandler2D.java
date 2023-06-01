package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Key;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.input.InputHandlerTypes;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Scheduler;

public class TransformInputHandler2D extends InputHandler {

	public static final Key KEY_MOVE_UP = new Key(Key.KEY_W, Key.KEY_UP, Key.KEY_UNKNOWN, Key.GAMEPAD_BUTTON_DPAD_UP);
	public static final Key KEY_MOVE_DOWN = new Key(Key.KEY_S, Key.KEY_DOWN, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_DPAD_DOWN);
	public static final Key KEY_MOVE_LEFT = new Key(Key.KEY_A, Key.KEY_LEFT, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_DPAD_LEFT);
	public static final Key KEY_MOVE_RIGHT = new Key(Key.KEY_D, Key.KEY_RIGHT, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_DPAD_RIGHT);
	public static final Key KEY_CLOSE_GAME = new Key(Key.KEY_ESCAPE, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_START);
	public static final Key KEY_FULLSCREEN = new Key(Key.KEY_F12, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_BACK);

	public static final float MOVEMENT_SPEED = 1000.0f;
	public static final float GAMEPAD_LEWAY = 0.3f;

	private PhysicObject physicObject;
	private long cooldown;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		physicObject = entity.getSystem(Engine.PHYSIC_SYSTEM);

		setGamepadLeftStickMove((pos) -> {
			if (pos.x < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.LEFT, MOVEMENT_SPEED));
			else if (pos.x > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.RIGHT, MOVEMENT_SPEED));
			if (pos.y < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.DOWN, MOVEMENT_SPEED));
			else if (pos.y > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.UP, MOVEMENT_SPEED));
		});

		setKey(KEY_CLOSE_GAME, Engine.getInstance()::shutdown);
		setKey(KEY_FULLSCREEN, () -> {
			if (System.currentTimeMillis() - cooldown > 1000) {
				cooldown = System.currentTimeMillis();
				Scheduler.runTaskSynchron(Engine.getInstance().getWindow()::toggleBorderless);
			}
		});

		setKey(KEY_MOVE_UP, () -> physicObject.accelerate(Vector2f.mult(Vector2f.UP, MOVEMENT_SPEED)));
		setKey(KEY_MOVE_DOWN, () -> physicObject.accelerate(Vector2f.mult(Vector2f.DOWN, MOVEMENT_SPEED)));
		setKey(KEY_MOVE_LEFT, () -> physicObject.accelerate(Vector2f.mult(Vector2f.LEFT, MOVEMENT_SPEED)));
		setKey(KEY_MOVE_RIGHT, () -> physicObject.accelerate(Vector2f.mult(Vector2f.RIGHT, MOVEMENT_SPEED)));
	}

	@Override
	public InputHandlerType<TransformInputHandler2D> getType() {
		return InputHandlerTypes.TRANSFORM_2D;
	}
}
