package org.jgine.system.systems.ui;

import org.jgine.system.systems.script.Script;

public interface UIScript extends Script {

	public void onEnable(UIWindow window);

	public void onDisable(UIWindow window);

	public void onUpdate(UIWindow window);
}
