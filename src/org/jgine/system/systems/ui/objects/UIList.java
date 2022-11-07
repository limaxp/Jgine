package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow;

public class UIList extends UIWindow {

	protected float elementHeight = 0.05f;

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
	public void load(Map<String, Object> data) {
		this.elementHeight = YamlHelper.toFloat(data.get("elementHeight"), 0.05f);
		super.load(data);
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.elementHeight = in.readFloat();
		super.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(elementHeight);
		super.save(out);
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

	@Override
	public int removeChild(UIObject child) {
		int index = super.removeChild(child);
		placeChilds(index);
		return index;
	}

	@Override
	public UIObject removeChild(int index) {
		UIObject child = super.removeChild(index);
		placeChilds(index);
		return child;
	}

	public void setElementHeight(float elementHeight) {
		this.elementHeight = elementHeight;
		placeChilds(0);
	}

	public float getElementHeight() {
		return elementHeight;
	}

	protected void placeChilds(int index) {
		List<UIObject> childs = getChilds();
		int size = childs.size();
		for (int i = index; i < size; i++)
			placeChild(childs.get(i), i);
	}

	protected void placeChild(UIObject child, int index) {
		child.set(0, 1 - (elementHeight * (index + 1)), 1, elementHeight);
	}
}
