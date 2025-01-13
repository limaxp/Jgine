package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.render.Renderer;
import org.jgine.system.data.ListSystemScene;
import org.jgine.utils.collection.list.IdentityArrayList;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector2i;

public class UIScene extends ListSystemScene<UISystem, UIWindow> {

	private static final List<UIObject> FOCUS_OBJECTS_EMPTY = new IdentityArrayList<UIObject>();

	float mouseX, mouseY;
	private List<UIObject> focusObjects;
	private UIObject clickedObject;
	private int clickedKey;

	public UIScene(UISystem system, Scene scene) {
		super(system, scene, UIWindow.class);
		focusObjects = FOCUS_OBJECTS_EMPTY;
	}

	@Override
	public void free() {
		synchronized (objects) {
			for (int i = 0; i < size; i++)
				objects[i].free();
		}
	}

	@Override
	public void initObject(Entity entity, UIWindow object) {
		object.entity = entity;
		object.scene = this;
		object.onEnable();
		for (UIObject child : object.childs) {
			if (child instanceof UIWindow)
				initObject(entity, (UIWindow) child);
			else
				child.onEnable();
		}
	}

	@Override
	public UIWindow removeObject(int index) {
		UIWindow object = super.removeObject(index);
		disableObject(object);
		return object;
	}

	private void disableObject(UIWindow object) {
		object.onDisable();
		for (UIObject child : object.childs) {
			if (child instanceof UIWindow)
				disableObject((UIWindow) child);
			else {
				child.onDisable();
				child.free();
			}
		}
		object.free();
	}

	@Override
	public void update(UpdateTask update) {
		Vector2f cursorPos = Input.getCursorPos();
		Vector2i windowSize = Input.getWindowSize();
		mouseX = cursorPos.x / windowSize.x;
		mouseY = 1 - cursorPos.y / windowSize.y;

		synchronized (objects) {
			UIObject focusObject = null;
			for (int i = size - 1; i >= 0; i--) {
				UIWindow window = objects[i];
				if (insideCheck(window, mouseX, mouseY)) {
					focusObject = focusCheck(window, mouseX, mouseY);
					break;
				}
			}
			focus(focusObject);
			clickCheck(focusObject);
			scrollCheck(focusObject);
		}
		update.finish(system);
	}

	private void focus(UIObject object) {
		if (focusObjects != FOCUS_OBJECTS_EMPTY) {
			if (object == focusObjects.get(0))
				return;

			if (object == null) {
				for (UIObject obj : focusObjects) {
					obj.isFocused = false;
					obj.onDefocus();
				}
				focusObjects = FOCUS_OBJECTS_EMPTY;
				return;
			}
		}
		if (object == null)
			return;

		List<UIObject> focusObjectsNew = new IdentityArrayList<UIObject>();
		focusObjectsNew.add(object);
		addTopWindows(focusObjectsNew, object);
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

	private void clickCheck(UIObject focusObject) {
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
		UIWindow topWindow = objects[size - 1];
		if (topWindow.isFloating())
			return;
		UIWindow newTopWindow = getTopWindow(focusObject);
		if (newTopWindow != objects[size - 1]) {
			objects[getTopWindowIndex(newTopWindow)] = objects[size - 1];
			objects[size - 1] = newTopWindow;
		}
	}

	private void scrollCheck(UIObject focusObject) {
		float scroll = Input.getMouse().getScroll();
		if (scroll != 0 && focusObject != null)
			focusObject.onScroll(scroll);
	}

	@Override
	public void render(float dt) {
		// TODO gets rendered for every camera!
		Renderer.setShader(Renderer.TEXTURE_SHADER);
		for (int i = 0; i < size; i++)
			objects[i].render();
	}

	@Override
	public Entity getEntity(int index) {
		return objects[index].entity;
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			UIWindow object = new UIWindow();
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			objects[i].save(out);
	}

	private int getTopWindowIndex(UIWindow object) {
		for (int i = 0; i < size; i++)
			if (objects[i] == object)
				return i;
		return 0;
	}

	private static void addTopWindows(List<UIObject> result, UIObject object) {
		UICompound window = object.parent;
		if (window == null)
			return;
		do {
			result.add(window);
		} while ((window = window.parent) != null);
	}

	private static UIWindow getTopWindow(UIObject object) {
		UICompound window = object.parent;
		if (window == null)
			return (UIWindow) object;
		while (window.parent != null)
			window = window.parent;
		return (UIWindow) window;
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
