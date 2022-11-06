package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.render.UIRenderer;
import org.jgine.system.data.ListSystemScene;

public class UIScene extends ListSystemScene<UISystem, UIWindow> {

	float mouseX, mouseY;
	private UIObject focusObject;
	private List<UIWindow> focusWindows;
	private UIObject clickedObject;

	public UIScene(UISystem system, Scene scene) {
		super(system, scene, UIWindow.class);
		focusWindows = new FastArrayList<UIWindow>();
	}

	@Override
	public void free() {
		for (int i = 0; i < size; i++)
			objects[i].free();
	}

	@Override
	public void initObject(Entity entity, UIWindow object) {
		object.scene = this;
	}

	@Override
	@Nullable
	public UIWindow removeObject(UIWindow object) {
		object.free();
		return super.removeObject(object);
	}

	@Override
	public void update() {
		Vector2f cursorPos = Input.getCursorPos();
		Vector2i windowSize = Input.getWindow().getSize();
		mouseX = cursorPos.x / windowSize.x;
		mouseY = 1 - cursorPos.y / windowSize.y;

		for (int i = size - 1; i >= 0; i--) {
			UIWindow window = objects[i];
			if (insideCheck(window, mouseX, mouseY)) {
				UIObject focusObject = focusCheck(window, mouseX, mouseY);
				focus(focusObject);
				clickCheck(focusObject, mouseX, mouseY);
				break;
			}
		}
	}

	private void focus(UIObject object) {
		if (object == focusObject)
			return;

		object.isFocused = true;
		object.onFocus();
		if (focusObject != null) {
			focusObject.isFocused = false;
			focusObject.onDefocus();
		}
		focusObject = object;

		List<UIWindow> focusWindowsNew = getTopWindows(object);
		for (UIWindow window : focusWindowsNew) {
			if (!this.focusWindows.contains(window)) {
				window.isFocused = true;
				window.onFocus();
			}
		}
		for (UIWindow window : focusWindows) {
			if (!focusWindowsNew.contains(window)) {
				window.isFocused = false;
				window.onDefocus();
			}
		}
		focusWindows = focusWindowsNew;
	}

	private void clickCheck(UIObject focusObject, float mouseX, float mouseY) {
		if (Input.getMouse().isKeyPressed(Key.MOUSE_BUTTON_LEFT))
			click(focusObject, mouseX, mouseY);
		else if (Input.getMouse().isKeyPressed(Key.MOUSE_BUTTON_RIGHT))
			click(focusObject, mouseX, mouseY);
		else if (clickedObject != null) {
			clickedObject.onRelease(mouseX, mouseY);
			clickedObject = null;
		}
	}

	private void click(UIObject focusObject, float mouseX, float mouseY) {
		if (clickedObject == null) {
			focusObject.onClick(mouseX, mouseY);
			clickedObject = focusObject;
			UIWindow topWindow = getTopWindow(focusObject);
			if (topWindow != objects[size - 1]) {
				int topWindowIndex = 0;
				for (int i = 0; i < size; i++) {
					if (objects[i] == topWindow) {
						topWindowIndex = i;
						break;
					}
				}
				objects[topWindowIndex] = objects[size - 1];
				objects[size - 1] = topWindow;
			}
		}
	}

	@Override
	public void render() {
		UIRenderer.setShader(UIRenderer.TEXTURE_SHADER);
		for (int i = 0; i < size; i++)
			objects[i].render();
	}

	@Override
	public UIWindow load(DataInput in) throws IOException {
		UIWindow object = new UIWindow();
		object.load(in);
		return object;
	}

	@Override
	public void save(UIWindow object, DataOutput out) throws IOException {
		object.save(out);
	}

	private static List<UIWindow> getTopWindows(UIObject object) {
		List<UIWindow> result = new FastArrayList<UIWindow>();
		UIWindow window = object.window;
		if (window == null) {
			result.add((UIWindow) object);
			return result;
		}
		result.add(window);
		while (window.window != null) {
			window = window.window;
			result.add(window);
		}
		return result;
	}

	private static UIWindow getTopWindow(UIObject object) {
		UIWindow window = object.window;
		if (window == null)
			return (UIWindow) object;
		while (window.window != null)
			window = window.window;
		return window;
	}

	private static UIObject focusCheck(UIWindow window, float mouseX, float mouseY) {
		float windowX = (mouseX - window.getX()) / window.getWidth();
		float windowY = (mouseY - window.getY()) / window.getHeight();
		for (UIObject child : window.getChilds()) {
			if (!insideCheck(child, windowX, windowY))
				continue;
			if (child instanceof UIWindow)
				return focusCheck(window, windowX, windowY);
			return child;
		}
		return window;
	}

	private static boolean insideCheck(UIObject object, float mouseX, float mouseY) {
		return mouseX >= object.getX() && mouseX <= object.getX() + object.getWidth() && mouseY >= object.getY()
				&& mouseY <= object.getY() + object.getHeight();
	}

}
