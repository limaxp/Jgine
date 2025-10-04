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
		forEach(UIWindow::free);
	}

	@Override
	public void onInit(Entity entity, UIWindow object) {
		object.entity = entity;
		object.scene = this;
		object.onEnable();
		for (UIObject child : object.childs) {
			if (child instanceof UIWindow)
				onInit(entity, (UIWindow) child);
			else
				child.onEnable();
		}
	}

	@Override
	public void onRemove(Entity entity, UIWindow object) {
		disableObject(object);
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
		for (int i = 0; i < size(); i++)
			get(i).preRender();

		for (int i = 0; i < size(); i++) {
			UIWindow window = get(i);
			UIRenderer.renderQuad(window.getTransform(), window.renderTargetMaterial);
		}
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).entity;
	}

	@Override
	public Transform getTransform(int index) {
		return getEntity(index).transform;
	}

	@Override
	protected void saveData(UIWindow object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected UIWindow loadData(DataInput in) throws IOException {
		UIWindow object = new UIWindow();
		object.load(in);
		return object;
	}

	public void setTopWindow(UIObject object) {
		int size = size();
		UIWindow topWindow = get(size - 1);
		if (topWindow.isFloating())
			return;
		UIWindow newTopWindow = getTopWindow(object);
		if (newTopWindow != get(size - 1))
			swap(getTopWindowIndex(newTopWindow), size - 1);
	}

	private int getTopWindowIndex(UIWindow object) {
		for (int i = 0; i < size(); i++)
			if (get(i) == object)
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
