package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.input.InputHandlerTypes;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.physic.PhysicSystem;

public class CameraInputHandler extends InputHandler {

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
	public static final float MOVEMENT_SPEED = 2000.0f;
	public static final float GAMEPAD_LEWAY = 0.3f;

	private PhysicObject physicObject;
	private Camera camera;
	private Vector2f lastCursorPosition;
	private long cooldown;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		physicObject = entity.getSystem(PhysicSystem.class);
		camera = entity.getSystem(CameraSystem.class);
		lastCursorPosition = Input.getCursorPos();

		setMouseMove((cursorPosition) -> {
			float deltaX = cursorPosition.x - lastCursorPosition.x;
			float deltaY = cursorPosition.y - lastCursorPosition.y;
			camera.rotateY(deltaX * ROTATION_SPEED);
			camera.rotateX(deltaY * ROTATION_SPEED);
			lastCursorPosition = cursorPosition;
		});

		setGamepadRightStickMove((pos) -> {
			camera.rotateY(pos.x * ROTATION_SPEED * 10);
			camera.rotateX(pos.y * ROTATION_SPEED * 10);
		});

		setGamepadLeftStickMove((pos) -> {
			if (pos.x < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(camera.getLeft(), MOVEMENT_SPEED));
			else if (pos.x > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(camera.getRight(), MOVEMENT_SPEED));
			if (pos.y < -GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(camera.getForward(), MOVEMENT_SPEED));
			else if (pos.y > GAMEPAD_LEWAY)
				physicObject.accelerate(Vector3f.mult(camera.getForward(), -MOVEMENT_SPEED));
		});

		setKey(KEY_MOVE_FORWARD, () -> physicObject.accelerate(Vector3f.mult(camera.getForward(), MOVEMENT_SPEED)));
		setKey(KEY_MOVE_BACK, () -> physicObject.accelerate(Vector3f.mult(camera.getForward(), -MOVEMENT_SPEED)));
		setKey(KEY_MOVE_LEFT, () -> physicObject.accelerate(Vector3f.mult(camera.getLeft(), MOVEMENT_SPEED)));
		setKey(KEY_MOVE_RIGHT, () -> physicObject.accelerate(Vector3f.mult(camera.getRight(), MOVEMENT_SPEED)));
		setKey(KEY_MOVE_UP, () -> physicObject.accelerate(Vector3f.mult(Vector3f.UP, MOVEMENT_SPEED)));
		setKey(KEY_MOVE_DOWN, () -> physicObject.accelerate(Vector3f.mult(Vector3f.DOWN, MOVEMENT_SPEED)));
		setKey(KEY_CLOSE_GAME, Engine.getInstance()::shutdown);
		setKey(KEY_FULLSCREEN, () -> {
			if (System.currentTimeMillis() - cooldown > 1000) {
				cooldown = System.currentTimeMillis();
				Scheduler.runTaskSynchron(Engine.getInstance().getWindow()::toggleBorderless);
			}
		});
	}

	@Override
	public InputHandlerType<CameraInputHandler> getType() {
		return InputHandlerTypes.CAMERA;
	}
}