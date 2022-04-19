package org.jgine.system.systems.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Window;
import org.jgine.core.entity.Entity;
import org.jgine.core.input.Input;
import org.jgine.core.input.Key;
import org.jgine.core.manager.SystemManager;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.render.UIRenderer;
import org.jgine.system.data.ListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class UIScene extends ListSystemScene<UISystem, UIObject> {

	int mouseX, mouseY;
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
		Window window = Engine.getInstance().getWindow();
		mouseX = (int) (cursorPos.x / window.getWidth() * UIObject.RASTER_SIZE);
		mouseY = (int) (UIObject.RASTER_SIZE - cursorPos.y / window.getHeight() * UIObject.RASTER_SIZE);
		if (mouseX > UIObject.RASTER_SIZE)
			mouseX = UIObject.RASTER_SIZE;
		else if (mouseX < 0)
			mouseX = 0;
		if (mouseY > UIObject.RASTER_SIZE)
			mouseY = UIObject.RASTER_SIZE;
		else if (mouseY < 0)
			mouseY = 0;

		for (int i = 0; i < size; i++)
			hoverCheck(objects[i], mouseX, mouseY);

		if (Input.isMousePressed(Key.MOUSE_1)) {
			if (clickedObject == null) {
				for (int i = 0; i < size; i++) {
					if (insideCheck(objects[i], mouseX, mouseY))
						clickedObject = clickCheck(objects[i], mouseX, mouseY);
				}
			}
		}
		else if (clickedObject != null) {
			clickedObject.onRelease(mouseX, mouseY);
			clickedObject = null;
		}
	}

	private static UIObject clickCheck(UIObject object, int mouseX, int mouseY) {
		if (object instanceof UIWindow)
			return clickCheck((UIWindow) object, mouseX, mouseY);

		if (insideCheck(object, mouseX, mouseY)) {
			object.onClick(mouseX, mouseY);
			return object;
		}
		return null;
	}

	private static UIObject clickCheck(UIWindow window, int mouseX, int mouseY) {
		int windowX = (int) ((float) (mouseX - window.getX()) / window.getWidth() * UIObject.RASTER_SIZE);
		int windowY = (int) ((float) (mouseY - window.getY()) / window.getHeight() * UIObject.RASTER_SIZE);
		for (UIObject child : window.getChilds()) {
			if (insideCheck(child, windowX, windowY)) {
				child.onClick(windowX, windowY);
				return child;
			}
		}
		window.onClick(mouseX, mouseY);
		return window;
	}

	private static void hoverCheck(UIObject object, int mouseX, int mouseY) {
		if (object instanceof UIWindow) {
			hoverCheck((UIWindow) object, mouseX, mouseY);
			return;
		}
		if (insideCheck(object, mouseX, mouseY)) {
			if (!object.isFocused) {
				object.isFocused = true;
				object.onFocus();
			}
		}
		else if (object.isFocused) {
			object.isFocused = false;
			object.onDefocus();
		}
	}

	private static void hoverCheck(UIWindow window, int mouseX, int mouseY) {
		if (insideCheck(window, mouseX, mouseY)) {
			if (!window.isFocused) {
				window.isFocused = true;
				window.onFocus();
			}
			int windowX = (int) ((float) (mouseX - window.getX()) / window.getWidth() * UIObject.RASTER_SIZE);
			int windowY = (int) ((float) (mouseY - window.getY()) / window.getHeight() * UIObject.RASTER_SIZE);
			for (UIObject child : window.getChilds()) {
				if (insideCheck(child, windowX, windowY)) {
					if (!child.isFocused) {
						child.isFocused = true;
						child.onFocus();
					}
				}
				else if (child.isFocused) {
					child.isFocused = false;
					child.onDefocus();
				}
			}
		}
		else if (window.isFocused) {
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

	private static boolean insideCheck(UIObject object, int mouseX, int mouseY) {
		return mouseX >= object.getX() && mouseX <= object.getX() + object.getWidth()
				&& mouseY >= object.getY() && mouseY <= object.getY() + object.getHeight();
	}

	@Override
	public void render() {
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		UIRenderer.setCamera(camera);
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
