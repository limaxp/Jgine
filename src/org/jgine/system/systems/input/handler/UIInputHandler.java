package org.jgine.system.systems.input.handler;

import java.util.List;

import org.jgine.core.Engine;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.system.systems.input.InputHandler;
import org.jgine.system.systems.ui.UICompound;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIScene;
import org.jgine.system.systems.ui.UIWindow;
import org.jgine.utils.collection.list.IdentityArrayList;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector2i;

public class UIInputHandler extends InputHandler {

	public static final Key KEY_UP = new Key(Key.KEY_W, Key.KEY_UP, Key.KEY_UNKNOWN, Key.GAMEPAD_BUTTON_DPAD_UP);
	public static final Key KEY_DOWN = new Key(Key.KEY_S, Key.KEY_DOWN, Key.KEY_UNKNOWN, Key.GAMEPAD_BUTTON_DPAD_DOWN);
	public static final Key KEY_ENTER = new Key(Key.KEY_ENTER, Key.KEY_UNKNOWN, Key.MOUSE_BUTTON_LEFT,
			Key.GAMEPAD_BUTTON_A);

	private static final List<UIObject> FOCUS_OBJECTS_EMPTY = new IdentityArrayList<UIObject>();

	private List<UIObject> focusObjects = FOCUS_OBJECTS_EMPTY;
	private UIObject focusObject;
	private UIObject clickedObject;
	private Vector2f lastMousePos = Vector2f.NULL;

	@Override
	protected void init(Entity entity) {
		setMouseMove(this::onMouseMove);
		setMouseScroll(this::onScroll);
		press(KEY_UP, this::onClickUp);
		press(KEY_DOWN, this::onClickDown);
		press(KEY_ENTER, (time) -> onClickKey(time, Key.KEY_ENTER)); // TODO pass real key!
		release(KEY_ENTER, (time) -> onReleaseKey(time, Key.KEY_ENTER)); // TODO pass real key!
	}

	private void onMouseMove(Vector2f cursorPos) {
		if (lastMousePos.equals(cursorPos))
			return;
		lastMousePos = cursorPos;
		Vector2i windowSize = Input.getWindowSize();
		float mouseX = cursorPos.x / windowSize.x;
		float mouseY = 1 - cursorPos.y / windowSize.y;

		focusObject = null;
		for (UIWindow window : getEntity().<UIWindow>getSystems(Engine.UI_SYSTEM)) {
			if (insideCheck(window, mouseX, mouseY)) {
				focusObject = focusCheck(window, mouseX, mouseY);
				break;
			}
		}
		focus();
	}

	private void onScroll(float scroll) {
		if (scroll != 0 && focusObject != null)
			focusObject.onScroll(scroll);
	}

	private void onClickKey(int time, int key) {
		if (time != 1 || focusObject == null)
			return;
		focusObject.onClick(key);
		clickedObject = focusObject;
		UIWindow window = focusObject instanceof UIWindow ? (UIWindow) focusObject : focusObject.getWindow();
		window.getScene().setTopWindow(focusObject);
	}

	private void onReleaseKey(int time, int key) {
		if (focusObject == null)
			return;
		clickedObject.onRelease(key);
		clickedObject = null;
	}

	private void onClickUp(int time) {
		if (time != 1 && time % 10 != 0)
			return;

		if (focusObject == null)
			focusObject = getEntity().getSystem(Engine.UI_SYSTEM);
		UICompound parent = focusObject.hasParent() ? focusObject.getParent() : (UICompound) focusObject;
		int index = parent.getChilds().indexOf(focusObject);
		if (index <= 0)
			index = parent.getChilds().size() - 1;
		else
			index--;

		focusObject = parent.getChilds().get(index);
		focus();
	}

	private void onClickDown(int time) {
		if (time != 1 && time % 10 != 0)
			return;

		if (focusObject == null)
			focusObject = getEntity().getSystem(Engine.UI_SYSTEM);
		UICompound parent = focusObject.hasParent() ? focusObject.getParent() : (UICompound) focusObject;
		int index = parent.getChilds().indexOf(focusObject);
		if (index < 0 || index == parent.getChilds().size() - 1)
			index = 0;
		else
			index++;

		focusObject = parent.getChilds().get(index);
		focus();
	}

	private void focus() {
		if (focusObjects != FOCUS_OBJECTS_EMPTY) {
			if (focusObject == focusObjects.get(0))
				return;

			if (focusObject == null) {
				for (UIObject obj : focusObjects) {
					obj.isFocused = false;
					obj.onDefocus();
				}
				focusObjects = FOCUS_OBJECTS_EMPTY;
				return;
			}
		}
		if (focusObject == null)
			return;

		List<UIObject> focusObjectsNew = new IdentityArrayList<UIObject>();
		focusObjectsNew.add(focusObject);
		UIScene.addTopWindows(focusObjectsNew, focusObject);
		for (UIObject uiObject : focusObjectsNew) {
			if (focusObjects.contains(uiObject))
				continue;
			uiObject.isFocused = true;
			uiObject.onFocus();
		}
		for (UIObject uiObject : focusObjects) {
			if (focusObjectsNew.contains(uiObject))
				continue;
			uiObject.isFocused = false;
			uiObject.onDefocus();
		}
		focusObjects = focusObjectsNew;
	}

	private static UIObject focusCheck(UICompound window, float mouseX, float mouseY) {
		float windowX = (mouseX - window.getX()) / window.getWidth();
		float windowY = (mouseY - window.getY()) / window.getHeight();
		if (window instanceof UIWindow) {
			UIWindow w = (UIWindow) window;
			float borderWidth = window.getWidth() * (1.0f - w.getBorderSize());
			float borderHeight = window.getHeight() * (1.0f - w.getBorderSize());
			float borderX = window.getX() + (window.getWidth() * w.getBorderSize() * 0.5f);
			float borderY = window.getY() + (window.getHeight() * w.getBorderSize() * 0.5f);
			windowX = (mouseX - borderX) / borderWidth;
			windowY = (mouseY - borderY) / borderHeight;

			float width = Math.abs(w.getViewWidth());
			float height = Math.abs(w.getViewHeight());
			windowX = windowX * width + w.getScrollX() + (1.0f - width);
			windowY = windowY * height + w.getScrollY() + (1.0f - height);
		}

		for (UIObject child : window.getVisibleChilds()) {
			if (!insideCheck(child, windowX, windowY))
				continue;
			if (child instanceof UICompound)
				return focusCheck((UICompound) child, windowX, windowY);
			return child;
		}
		return window;
	}

	private static boolean insideCheck(UIObject object, float mouseX, float mouseY) {
		return mouseX >= object.getX() && mouseX <= object.getX() + object.getWidth() && mouseY >= object.getY()
				&& mouseY <= object.getY() + object.getHeight();
	}
}
