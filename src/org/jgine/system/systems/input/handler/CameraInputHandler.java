package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.InputDevice;
import org.jgine.core.input.Key;
import org.jgine.core.input.device.Gamepad;
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

	private static final Key KEY_MOVE_FORWARD = new Key(Key.KEY_W, Key.KEY_UP, Key.KEY_UNKNOWN);
	private static final Key KEY_MOVE_BACK = new Key(Key.KEY_S, Key.KEY_DOWN, Key.KEY_UNKNOWN);
	private static final Key KEY_MOVE_LEFT = new Key(Key.KEY_A, Key.KEY_LEFT, Key.KEY_UNKNOWN);
	private static final Key KEY_MOVE_RIGHT = new Key(Key.KEY_D, Key.KEY_RIGHT, Key.KEY_UNKNOWN);
	private static final Key KEY_MOVE_UP = new Key(Key.KEY_SPACE, Key.KEY_RCONTROL, Key.GAMEPAD_BUTTON_CROSS);
	private static final Key KEY_MOVE_DOWN = new Key(Key.KEY_LSHIFT, Key.KEY_RSHIFT, Key.GAMEPAD_BUTTON_CIRCLE);
	private static final Key KEY_CLOSE_GAME = new Key(Key.KEY_ESCAPE, Key.GAMEPAD_BUTTON_START);
	private static final Key KEY_FULLSCREEN = new Key(Key.KEY_F12, Key.GAMEPAD_BUTTON_BACK);

	private static final float ROTATION_SPEED = 0.005f;
	private static final float MOVEMENT_SPEED = 0.1f;
	private static final float GAMEPAD_LEWAY = 0.3f;

	private PhysicObject physicObject;
	private Camera cam;

	private float lastMouseX;
	private float lastMouseY;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		physicObject = entity.getSystem(PhysicSystem.class);
		cam = entity.getSystem(CameraSystem.class);

		Vector2f cursorPos = Input.getCursorPos();
		lastMouseX = cursorPos.x;
		lastMouseY = cursorPos.y;
	}

	@Override
	public void checkInput(InputDevice inputDevice) {
		if (inputDevice.isMouse()) {
			Vector2f cursorPos = Input.getCursorPos();
			float deltaX = cursorPos.x - lastMouseX;
			float deltaY = cursorPos.y - lastMouseY;
			cam.rotateY(deltaX * ROTATION_SPEED);
			cam.rotateX(deltaY * ROTATION_SPEED);
			lastMouseX = cursorPos.x;
			lastMouseY = cursorPos.y;
			return;

		} else if (inputDevice.isKeyboard()) {
			if (inputDevice.isKeyPressed(KEY_MOVE_FORWARD))
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), MOVEMENT_SPEED));
			if (inputDevice.isKeyPressed(KEY_MOVE_BACK))
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), -MOVEMENT_SPEED));
			if (inputDevice.isKeyPressed(KEY_MOVE_LEFT))
				physicObject.addVelocity(Vector3f.mult(cam.getLeft(), MOVEMENT_SPEED));
			if (inputDevice.isKeyPressed(KEY_MOVE_RIGHT))
				physicObject.addVelocity(Vector3f.mult(cam.getRight(), MOVEMENT_SPEED));

		} else if (inputDevice.isGamepad()) {
			Gamepad gamepad = (Gamepad) inputDevice;
			float rightX = gamepad.getAxisRightX();
			float rightY = gamepad.getAxisRightY();
			cam.rotateY(rightX * ROTATION_SPEED * 10);
			cam.rotateX(rightY * ROTATION_SPEED * 10);

			float leftX = gamepad.getAxisLeftX();
			float leftY = gamepad.getAxisLeftY();
			if (leftX < -GAMEPAD_LEWAY)
				physicObject.addVelocity(Vector3f.mult(cam.getLeft(), MOVEMENT_SPEED));
			else if (leftX > GAMEPAD_LEWAY)
				physicObject.addVelocity(Vector3f.mult(cam.getRight(), MOVEMENT_SPEED));
			if (leftY < -GAMEPAD_LEWAY)
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), MOVEMENT_SPEED));
			else if (leftY > GAMEPAD_LEWAY)
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), -MOVEMENT_SPEED));
		}

		if (inputDevice.isKeyPressed(KEY_MOVE_UP))
			physicObject.addVelocity(Vector3f.mult(Vector3f.UP, MOVEMENT_SPEED));
		if (inputDevice.isKeyPressed(KEY_MOVE_DOWN))
			physicObject.addVelocity(Vector3f.mult(Vector3f.DOWN, MOVEMENT_SPEED));
		if (inputDevice.isKeyPressed(KEY_CLOSE_GAME))
			Engine.getInstance().shutdown();
		if (inputDevice.isKeyPressed(KEY_FULLSCREEN))
			Scheduler.runTaskSynchron(() -> Engine.getInstance().getWindow().toggleFullScreen());
	}

	@Override
	public InputHandlerType<CameraInputHandler> getType() {
		return InputHandlerTypes.CAMERA;
	}
}