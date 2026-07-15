package jgine.system.input.handler;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.core.input.Key;
import jgine.system.input.InputHandler;
import jgine.system.physic.PhysicObject;
import jgine.utils.math.FastMath;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.scheduler.Scheduler;

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
	protected void init(Entity entity) {
		physicObject = entity.get(Engine.PHYSIC);

		setLeftStickMove((pos) -> {
			if (FastMath.abs(pos.x) > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.RIGHT, pos.x * MOVEMENT_SPEED));
			if (FastMath.abs(pos.y) > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(Vector2f.DOWN, pos.y * MOVEMENT_SPEED));
		});

		press(KEY_CLOSE_GAME, (_) -> Engine.getInstance().shutdown());
		press(KEY_FULLSCREEN, (_) -> {
			if (System.currentTimeMillis() - cooldown > 1000) {
				cooldown = System.currentTimeMillis();
				Scheduler.runTask(Engine.getInstance().window::toggleBorderless);
			}
		});

		press(KEY_MOVE_UP, (_) -> physicObject.accelerate(Vector2f.mult(Vector2f.UP, MOVEMENT_SPEED)));
		press(KEY_MOVE_DOWN, (_) -> physicObject.accelerate(Vector2f.mult(Vector2f.DOWN, MOVEMENT_SPEED)));
		press(KEY_MOVE_LEFT, (_) -> physicObject.accelerate(Vector2f.mult(Vector2f.LEFT, MOVEMENT_SPEED)));
		press(KEY_MOVE_RIGHT, (_) -> physicObject.accelerate(Vector2f.mult(Vector2f.RIGHT, MOVEMENT_SPEED)));
	}
}
