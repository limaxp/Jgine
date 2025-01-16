package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Scheduler;

public class TransformInputHandler extends InputHandler {

	public static final Key KEY_MOVE_FORWARD = new Key(Key.KEY_W, Key.KEY_UP, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN);
	public static final Key KEY_MOVE_BACK = new Key(Key.KEY_S, Key.KEY_DOWN, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN);
	public static final Key KEY_MOVE_LEFT = new Key(Key.KEY_A, Key.KEY_LEFT, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN);
	public static final Key KEY_MOVE_RIGHT = new Key(Key.KEY_D, Key.KEY_RIGHT, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN);
	public static final Key KEY_MOVE_UP = new Key(Key.KEY_SPACE, Key.KEY_RCONTROL, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_CROSS);
	public static final Key KEY_MOVE_DOWN = new Key(Key.KEY_LSHIFT, Key.KEY_RSHIFT, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_CIRCLE);
	public static final Key KEY_CLOSE_GAME = new Key(Key.KEY_ESCAPE, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_START);
	public static final Key KEY_FULLSCREEN = new Key(Key.KEY_F12, Key.KEY_UNKNOWN, Key.KEY_UNKNOWN,
			Key.GAMEPAD_BUTTON_BACK);

	public static final float ROTATION_SPEED = 0.005f;
	public static final float MOVEMENT_SPEED = 1000.0f;
	public static final float GAMEPAD_LEWAY = 0.3f;

	private Transform transform;
	private PhysicObject physicObject;
	private Vector2f lastCursorPosition;
	private long cooldown;

	@Override
	protected void init(Entity entity) {
		transform = entity.transform;
		physicObject = entity.getSystem(Engine.PHYSIC_SYSTEM);
		lastCursorPosition = Input.getCursorPos();

		setMouseMove((cursorPosition) -> {
			float deltaX = cursorPosition.x - lastCursorPosition.x;
			float deltaY = cursorPosition.y - lastCursorPosition.y;
			transform.rotateY(deltaX * ROTATION_SPEED);
			transform.rotateX(deltaY * ROTATION_SPEED);
			lastCursorPosition = cursorPosition;
		});

		setRightStickMove((pos) -> {
			transform.rotateY(pos.x * ROTATION_SPEED * 10);
			transform.rotateX(pos.y * ROTATION_SPEED * 10);
		});

		setLeftStickMove((pos) -> {
			if (pos.x < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector3f.getLeft(transform.getLocalRotation()), MOVEMENT_SPEED));
			else if (pos.x > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector3f.getRight(transform.getLocalRotation()), MOVEMENT_SPEED));
			if (pos.y < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), MOVEMENT_SPEED));
			else if (pos.y > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), -MOVEMENT_SPEED));
		});

		press(KEY_MOVE_FORWARD,
				() -> physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), MOVEMENT_SPEED)));
		press(KEY_MOVE_BACK,
				() -> physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), -MOVEMENT_SPEED)));
		press(KEY_MOVE_LEFT, () -> physicObject
				.accelerate(Vector3f.mult(Vector3f.getLeft(transform.getLocalRotation()), MOVEMENT_SPEED)));
		press(KEY_MOVE_RIGHT, () -> physicObject
				.accelerate(Vector3f.mult(Vector3f.getRight(transform.getLocalRotation()), MOVEMENT_SPEED)));
		press(KEY_MOVE_UP, () -> physicObject.accelerate(Vector3f.mult(Vector3f.UP, MOVEMENT_SPEED)));
		press(KEY_MOVE_DOWN, () -> physicObject.accelerate(Vector3f.mult(Vector3f.DOWN, MOVEMENT_SPEED)));
		press(KEY_CLOSE_GAME, Engine.getInstance()::shutdown);
		press(KEY_FULLSCREEN, () -> {
			if (System.currentTimeMillis() - cooldown > 1000) {
				cooldown = System.currentTimeMillis();
				Scheduler.runTaskSynchron(Engine.getInstance().getWindow()::toggleBorderless);
			}
		});
	}
}
