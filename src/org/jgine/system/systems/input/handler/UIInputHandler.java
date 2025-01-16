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
			Key.GAMEPAD_BUTTON_X);

	private static final List<UIObject> FOCUS_OBJECTS_EMPTY = new IdentityArrayList<UIObject>();

	private List<UIObject> focusObjects = FOCUS_OBJECTS_EMPTY;
	private UIObject focusObject;
	private UIObject clickedObject;
	private int clickedKey;

	@Override
	protected void init(Entity entity) {
		setMouseMove(this::onMouseMove);
		setMouseScroll(this::onScroll);

		press(KEY_UP, () -> {

		});

		press(KEY_DOWN, () -> {

		});

		press(KEY_ENTER, () -> {

		});
	}

	private void onMouseMove(Vector2f cursorPos) {
		Vector2i windowSize = Input.getWindowSize();
		float mouseX = cursorPos.x / windowSize.x;
		float mouseY = 1 - cursorPos.y / windowSize.y;

		focusObject = null;
		getEntity().forSystems(Engine.UI_SYSTEM, (UIWindow window) -> {
			if (insideCheck(window, mouseX, mouseY))
				focusObject = focusCheck(window, mouseX, mouseY);
		});

		focus();
		clickCheck();
	}

	private void onScroll(float scroll) {
		if (scroll != 0 && focusObject != null)
			focusObject.onScroll(scroll);
	}

	private void clickCheck() {
		if (Input.getMouse().isKeyPressed(Key.MOUSE_BUTTON_LEFT))
			click(focusObject, Key.MOUSE_BUTTON_LEFT);
		else if (Input.getMouse().isKeyPressed(Key.MOUSE_BUTTON_RIGHT))
			click(focusObject, Key.MOUSE_BUTTON_RIGHT);
		else if (clickedObject != null) {
			clickedObject.onRelease(clickedKey);
			clickedObject = null;
		}
	}

	private void click(UIObject focusObject, int key) {
		if (clickedObject != null || focusObject == null)
			return;

		focusObject.onClick(key);
		clickedObject = focusObject;
		clickedKey = key;
		focusObject.getWindow().getScene().setTopWindow(focusObject);
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
			if (!focusObjects.contains(uiObject)) {
				uiObject.isFocused = true;
				uiObject.onFocus();
			}
		}
		for (UIObject uiObject : focusObjects) {
			if (!focusObjectsNew.contains(uiObject)) {
				uiObject.isFocused = false;
				uiObject.onDefocus();
			}
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
