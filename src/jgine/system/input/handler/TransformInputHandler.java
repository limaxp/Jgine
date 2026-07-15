package jgine.system.input.handler;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.core.input.Input;
import jgine.core.input.Key;
import jgine.system.input.InputHandler;
import jgine.system.physic.PhysicObject;
import jgine.system.transform.Transform;
import jgine.utils.math.FastMath;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.scheduler.Scheduler;

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
		transform = entity.getTransform();
		physicObject = entity.get(Engine.PHYSIC);
		lastCursorPosition = Input.getCursorPos();

		setMouseMove((cursorPosition) -> {
			float deltaX = cursorPosition.x - lastCursorPosition.x;
			float deltaY = cursorPosition.y - lastCursorPosition.y;
			transform.rotateY(deltaX * ROTATION_SPEED);
			transform.rotateX(-deltaY * ROTATION_SPEED);
			lastCursorPosition = cursorPosition;
		});

		setRightStickMove((pos) -> {
			if (FastMath.abs(pos.x) > GAMEPAD_LEWAY)
				transform.rotateY(pos.x * ROTATION_SPEED * 10);
			if (FastMath.abs(pos.y) > GAMEPAD_LEWAY)
				transform.rotateX(pos.y * ROTATION_SPEED * 10);
		});

		setLeftStickMove((pos) -> {
			if (FastMath.abs(pos.x) > GAMEPAD_LEWAY)
				physicObject.accelerate(
						Vector3f.mult(Vector3f.getRight(transform.getLocalRotation()), pos.x * MOVEMENT_SPEED));
			if (FastMath.abs(pos.y) > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), -pos.y * MOVEMENT_SPEED));
		});

		press(KEY_MOVE_FORWARD,
				(_) -> physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), MOVEMENT_SPEED)));
		press(KEY_MOVE_BACK,
				(_) -> physicObject.accelerate(Vector3f.mult(transform.getLocalRotation(), -MOVEMENT_SPEED)));
		press(KEY_MOVE_LEFT, (_) -> physicObject
				.accelerate(Vector3f.mult(Vector3f.getLeft(transform.getLocalRotation()), MOVEMENT_SPEED)));
		press(KEY_MOVE_RIGHT, (_) -> physicObject
				.accelerate(Vector3f.mult(Vector3f.getRight(transform.getLocalRotation()), MOVEMENT_SPEED)));
		press(KEY_MOVE_UP, (_) -> physicObject.accelerate(Vector3f.mult(Vector3f.UP, MOVEMENT_SPEED)));
		press(KEY_MOVE_DOWN, (_) -> physicObject.accelerate(Vector3f.mult(Vector3f.DOWN, MOVEMENT_SPEED)));
		press(KEY_CLOSE_GAME, (_) -> Engine.getInstance().shutdown());
		press(KEY_FULLSCREEN, (_) -> {
			if (System.currentTimeMillis() - cooldown > 1000) {
				cooldown = System.currentTimeMillis();
				Scheduler.runTask(Engine.getInstance().window::toggleBorderless);
			}
		});
	}
}
