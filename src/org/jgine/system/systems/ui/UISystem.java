package org.jgine.system.systems.ui;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

public class UISystem extends EngineSystem {

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
