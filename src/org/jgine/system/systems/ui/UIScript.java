package org.jgine.system.systems.ui;

import org.jgine.system.systems.script.ScriptBase;

public interface UIScript extends ScriptBase {

	public void onEnable(UIWindow window);

	public void onDisable(UIWindow window);

	public void onUpdate(UIWindow window);
}
