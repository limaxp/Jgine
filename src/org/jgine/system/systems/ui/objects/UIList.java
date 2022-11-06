package org.jgine.system.systems.ui.objects;

import java.util.List;

import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow;

public class UIList extends UIWindow {

	private float childHeight = 0.05f;

	public UIList() {
	}

	public UIList(float scale) {
		super(scale);
	}

	public UIList(float width, float height) {
		super(width, height);
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

	@Override
	public void addChild(UIObject child) {
		super.addChild(child);
		int size = getChilds().size();
		placeChild(child, size - 1);
	}

	public void setChildHeight(float childHeight) {
		this.childHeight = childHeight;
		List<UIObject> childs = getChilds();
		int size = childs.size();
		for (int i = 0; i < size; i++)
			placeChild(childs.get(i), i);
	}

	public float getChildHeight() {
		return childHeight;
	}

	protected void placeChild(UIObject child, int index) {
		child.set(0, 1 - (childHeight * (index + 1)), 1, childHeight);
	}
}
