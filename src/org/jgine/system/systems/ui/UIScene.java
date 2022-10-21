package org.jgine.system.systems.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.render.UIRenderer;
import org.jgine.system.data.ListSystemScene;

public class UIScene extends ListSystemScene<UISystem, UIObject> {

	float mouseX, mouseY;
	private UIObject clickedObject;

	public UIScene(UISystem system, Scene scene) {
		super(system, scene, UIObject.class);
	}

	@Override
	public void free() {
		for (int i = 0; i < size; i++)
			objects[i].free();
	}

	@Override
	public void initObject(Entity entity, UIObject object) {
		if (object instanceof UIWindow)
			((UIWindow) object).scene = this;
		object.create(entity);
	}

	@Override
	@Nullable
	public UIObject removeObject(UIObject object) {
		object.free();
		return super.removeObject(object);
	}

	@Override
	public void update() {
		Vector2f cursorPos = Input.getCursorPos();
		Vector2i windowSize = Input.getWindow().getSize();
		mouseX = cursorPos.x / windowSize.x;
		mouseY = 1 - cursorPos.y / windowSize.y;

		for (int i = 0; i < size; i++)
			hoverCheck(objects[i], mouseX, mouseY);

		if (Input.getMouse().isKeyPressed(Key.MOUSE_BUTTON_1)) {
			if (clickedObject == null) {
				for (int i = 0; i < size; i++) {
					if (insideCheck(objects[i], mouseX, mouseY))
						clickedObject = clickCheck(objects[i], mouseX, mouseY);
				}
			}
		} else if (clickedObject != null) {
			clickedObject.onRelease(mouseX, mouseY);
			clickedObject = null;
		}
	}

	private static UIObject clickCheck(UIObject object, float mouseX, float mouseY) {
		if (object instanceof UIWindow)
			return clickCheck((UIWindow) object, mouseX, mouseY);

		if (insideCheck(object, mouseX, mouseY)) {
			object.onClick(mouseX, mouseY);
			return object;
		}
		return null;
	}

	private static UIObject clickCheck(UIWindow window, float mouseX, float mouseY) {
		float windowX = (mouseX - window.getX()) / window.getWidth();
		float windowY = (mouseY - window.getY()) / window.getHeight();
		for (UIObject child : window.getChilds()) {
			if (insideCheck(child, windowX, windowY)) {
				child.onClick(windowX, windowY);
				return child;
			}
		}
		window.onClick(mouseX, mouseY);
		return window;
	}

	private static void hoverCheck(UIObject object, float mouseX, float mouseY) {
		if (object instanceof UIWindow) {
			hoverCheck((UIWindow) object, mouseX, mouseY);
			return;
		}
		if (insideCheck(object, mouseX, mouseY)) {
			if (!object.isFocused) {
				object.isFocused = true;
				object.onFocus();
			}
		} else if (object.isFocused) {
			object.isFocused = false;
			object.onDefocus();
		}
	}

	private static void hoverCheck(UIWindow window, float mouseX, float mouseY) {
		if (insideCheck(window, mouseX, mouseY)) {
			if (!window.isFocused) {
				window.isFocused = true;
				window.onFocus();
			}
			float windowX = (mouseX - window.getX()) / window.getWidth();
			float windowY = (mouseY - window.getY()) / window.getHeight();
			for (UIObject child : window.getChilds()) {
				if (insideCheck(child, windowX, windowY)) {
					if (!child.isFocused) {
						child.isFocused = true;
						child.onFocus();
					}
				} else if (child.isFocused) {
					child.isFocused = false;
					child.onDefocus();
				}
			}
		} else if (window.isFocused) {
			window.isFocused = false;
			window.onDefocus();
			for (UIObject child : window.getChilds()) {
				if (child.isFocused) {
					child.isFocused = false;
					child.onDefocus();
				}
			}
		}
	}

	private static boolean insideCheck(UIObject object, float mouseX, float mouseY) {
		return mouseX >= object.getX() && mouseX <= object.getX() + object.getWidth() && mouseY >= object.getY()
				&& mouseY <= object.getY() + object.getHeight();
	}

	@Override
	public void render() {
		UIRenderer.setShader(UIRenderer.TEXTURE_SHADER);
		for (int i = 0; i < size; i++) {
			UIObject object = objects[i];
			object.render();
			if (object instanceof UIWindow)
				for (UIObject child : ((UIWindow) object).getChilds())
					child.render();
		}
	}
}
