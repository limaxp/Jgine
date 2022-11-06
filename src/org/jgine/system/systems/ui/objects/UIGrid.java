package org.jgine.system.systems.ui.objects;

import java.util.List;

import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow;

public class UIGrid extends UIWindow {

	private float childHeight = 0.05f;
	private float childWidth = 0.2f;

	public UIGrid() {
	}

	public UIGrid(float scale) {
		super(scale);
	}

	public UIGrid(float width, float height) {
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
	public UIObjectType<? extends UIGrid> getType() {
		return UIObjectTypes.GRID;
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

	public void setChildWidth(float childWidth) {
		this.childWidth = childWidth;
		List<UIObject> childs = getChilds();
		int size = childs.size();
		for (int i = 0; i < size; i++)
			placeChild(childs.get(i), i);
	}

	public float getChildWidth() {
		return childWidth;
	}

	protected void placeChild(UIObject child, int index) {
		float widthSize = childWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, 1 - (childHeight * (1 + height)), childWidth, childHeight);
	}
}