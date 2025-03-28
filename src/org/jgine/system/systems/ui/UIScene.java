package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.render.UIRenderer;
import org.jgine.system.data.ObjectSystemScene;

public class UIScene extends ObjectSystemScene<UISystem, UIWindow> {

	public UIScene(UISystem system, Scene scene) {
		super(system, scene, UIWindow.class, 10000);
	}

	@Override
	public void free() {
		for (int i = 0; i < size; i++)
			objects[i].free();
	}

	@Override
	public void init(Entity entity, UIWindow object) {
		object.entity = entity;
		object.scene = this;
		object.onEnable();
		for (UIObject child : object.childs) {
			if (child instanceof UIWindow)
				init(entity, (UIWindow) child);
			else
				child.onEnable();
		}
	}

	@Override
	public void remove(int index) {
		disableObject(get(index));
		super.remove(index);
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
	public void onRender(float dt) {
		Renderer.setShader(Renderer.TEXTURE_SHADER);
		for (int i = 0; i < size; i++)
			objects[i].preRender();

		for (int i = 0; i < size; i++) {
			UIWindow window = objects[i];
			UIRenderer.renderQuad(window.getTransform(), window.renderTargetMaterial);
		}
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

	public void setTopWindow(UIObject object) {
		UIWindow topWindow = objects[size - 1];
		if (topWindow.isFloating())
			return;
		UIWindow newTopWindow = getTopWindow(object);
		if (newTopWindow != objects[size - 1]) {
			objects[getTopWindowIndex(newTopWindow)] = objects[size - 1];
			objects[size - 1] = newTopWindow;
		}
	}

	private int getTopWindowIndex(UIWindow object) {
		for (int i = 0; i < size; i++)
			if (objects[i] == object)
				return i;
		return 0;
	}

	public static void addTopWindows(List<UIObject> result, UIObject object) {
		UICompound window = object.parent;
		if (window == null)
			return;
		do {
			result.add(window);
		} while ((window = window.parent) != null);
	}

	public static UIWindow getTopWindow(UIObject object) {
		UICompound window = object.parent;
		if (window == null)
			return (UIWindow) object;
		while (window.parent != null)
			window = window.parent;
		return (UIWindow) window;
	}
}
