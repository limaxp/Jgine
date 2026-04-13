package jgine.system.ui;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;

public class UISystem extends EngineSystem<UISystem, UIWindow> {

	public UISystem() {
		super("ui");
	}

	@Override
	public UIScene createScene(Scene scene) {
		return new UIScene(this, scene);
	}

	@Override
	public UIWindow load(Map<String, Object> data) {
		UIWindow object = new UIWindow();
		object.load(data);
		return object;
	}
}
