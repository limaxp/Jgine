package org.jgine.system.systems.input.handler;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Input.GamepadState;
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

	private static final Key KEY_MOVE_FORWARD = new Key(Key.KEY_W, Key.KEY_UP, Key.UNKNOWN);
	private static final Key KEY_MOVE_BACK = new Key(Key.KEY_S, Key.KEY_DOWN, Key.UNKNOWN);
	private static final Key KEY_MOVE_LEFT = new Key(Key.KEY_A, Key.KEY_LEFT, Key.UNKNOWN);
	private static final Key KEY_MOVE_RIGHT = new Key(Key.KEY_D, Key.KEY_RIGHT, Key.UNKNOWN);
	private static final Key KEY_MOVE_UP = new Key(Key.KEY_SPACE, Key.KEY_RCONTROL, Key.GAMEPAD_BUTTON_CROSS);
	private static final Key KEY_MOVE_DOWN = new Key(Key.KEY_LSHIFT, Key.KEY_RSHIFT, Key.GAMEPAD_BUTTON_CIRCLE);
	private static final Key KEY_CLOSE_GAME = new Key(Key.KEY_ESCAPE, Key.GAMEPAD_BUTTON_START);
	private static final Key KEY_FULLSCREEN = new Key(Key.KEY_F12, Key.GAMEPAD_BUTTON_BACK);

	private PhysicObject physicObject;
	private Camera cam;

	private double lastMouseX;
	private double lastMouseY;

	@Override
	protected void setEntity(Entity entity) {
		super.setEntity(entity);
		physicObject = entity.getSystem(PhysicSystem.class);
		cam = entity.getSystem(CameraSystem.class);
	}

	@Override
	public void checkInput() {
		if (inputSlot == Input.Slot.KEYBOARD) {
			Vector2f cursorPos = Input.getCursorPos();
			if (cursorPos.x > lastMouseX)
				cam.rotateY(0.015f);
			else if (cursorPos.x < lastMouseX)
				cam.rotateY(-0.015f);

			if (cursorPos.y > lastMouseY)
				cam.rotateX(0.015f);
			else if (cursorPos.y < lastMouseY)
				cam.rotateX(-0.015f);
			lastMouseX = cursorPos.x;
			lastMouseY = cursorPos.y;

			if (Input.isKeyPressed(KEY_MOVE_FORWARD))
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), 0.1f));
			if (Input.isKeyPressed(KEY_MOVE_BACK))
				physicObject.addVelocity(Vector3f.mult(cam.getForward(), -0.1f));
			if (Input.isKeyPressed(KEY_MOVE_LEFT))
				physicObject.addVelocity(Vector3f.mult(cam.getLeft(), 0.1f));
			if (Input.isKeyPressed(KEY_MOVE_RIGHT))
				physicObject.addVelocity(Vector3f.mult(cam.getRight(), 0.1f));
		} else {
			GamepadState state = Input.getGamepadState(inputSlot);
			float rightX = state.axes(Key.GAMEPAD_AXIS_RIGHT_X);
			float rightY = state.axes(Key.GAMEPAD_AXIS_RIGHT_Y);
			if (rightX > 1.5259022E-5 || rightX < -1.5259022E-5) {
				if (rightX > 0.3f)
					cam.rotateY(0.015f);
				else if (rightX < -0.3f)
					cam.rotateY(-0.015f);
			}
			if (rightY > 1.5259022E-5 || rightY < -1.5259022E-5) {
				if (rightY > 0.3f)
					cam.rotateX(0.015f);
				else if (rightY < -0.3f)
					cam.rotateX(-0.015f);
			}

			float leftX = state.axes(Key.GAMEPAD_AXIS_LEFT_X);
			float leftY = state.axes(Key.GAMEPAD_AXIS_LEFT_Y);
			if (leftX > 1.5259022E-5 || leftX < -1.5259022E-5) {
				if (leftX < -0.3f)
					physicObject.addVelocity(Vector3f.mult(cam.getLeft(), 0.1f));
				else if (leftX > 0.3f)
					physicObject.addVelocity(Vector3f.mult(cam.getRight(), 0.1f));
			}
			if (leftY > 1.5259022E-5 || leftY < -1.5259022E-5) {
				if (leftY < -0.3f)
					physicObject.addVelocity(Vector3f.mult(cam.getForward(), 0.1f));
				else if (leftY > 0.3f)
					physicObject.addVelocity(Vector3f.mult(cam.getForward(), -0.1f));
			}
		}

		if (Input.isKeyPressed(inputSlot, KEY_MOVE_UP))
			physicObject.addVelocity(Vector3f.mult(Vector3f.UP, 0.1f));
		if (Input.isKeyPressed(inputSlot, KEY_MOVE_DOWN))
			physicObject.addVelocity(Vector3f.mult(Vector3f.DOWN, 0.1f));

		if (Input.isKeyPressed(inputSlot, KEY_CLOSE_GAME))
			Engine.getInstance().shutdown();
		if (Input.isKeyPressed(inputSlot, KEY_FULLSCREEN))
			Scheduler.runTaskSynchron(() -> Engine.getInstance().getWindow().toggleFullScreen());
	}

	@Override
	public InputHandlerType<CameraInputHandler> getType() {
		return InputHandlerTypes.CAMERA;
	}
}