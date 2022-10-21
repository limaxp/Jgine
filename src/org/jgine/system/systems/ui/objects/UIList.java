package org.jgine.system.systems.ui.objects;

import org.jgine.core.entity.Entity;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UIList extends UIObject {

	public UIList() {
	}

	@Override
	protected void create(Entity entity) {
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
	}

	@Override
	public void onFocus() {
	}

	@Override
	public void onDefocus() {
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
	}

	@Override
	public void onRelease(float mouseX, float mouseY) {
	}

	@Override
	public UIObjectType<? extends UIList> getType() {
		return UIObjectTypes.LIST;
	}
}
