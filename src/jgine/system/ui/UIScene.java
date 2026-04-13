package jgine.system.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.render.Renderer;
import jgine.render.UIRenderer;
import jgine.system.ObjectSystemScene;
import jgine.system.transform.Transform;

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
		onInit(entity, (UICompound) object);
	}

	private void onInit(Entity entity, UICompound object) {
		object.entity = entity;
		object.onEnable();
		for (UIObject child : object.childs) {
			if (child instanceof UICompound)
				onInit(entity, (UICompound) child);
			else
				child.onEnable();
		}
	}

	@Override
	protected void onRemove(Entity entity, UIWindow object) {
		onRemove(entity, (UICompound) object);
	}

	private void onRemove(Entity entity, UICompound object) {
		object.onDisable();
		for (UIObject child : object.childs) {
			if (child instanceof UICompound)
				onRemove(entity, (UICompound) child);
			else
				child.onDisable();
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
		return getEntity(index).getTransform();
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
		UIWindow newTopWindow = object.getTopWindow();
		if (newTopWindow != get(size - 1))
			swap(getIndex(newTopWindow), size - 1);
	}

	private int getIndex(UIWindow object) {
		for (int i = 0; i < size(); i++)
			if (get(i) == object)
				return i;
		return 0;
	}
}
