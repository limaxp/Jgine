package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UIGrid extends UIList {

	protected float elementWidth = 0.2f;

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
	public void load(Map<String, Object> data) {
		elementWidth = YamlHelper.toFloat(data.get("elementWidth"), 0.02f);
		super.load(data);
	}

	@Override
	public void load(DataInput in) throws IOException {
		elementWidth = in.readFloat();
		super.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(elementWidth);
		super.save(out);
	}

	@Override
	public UIObjectType<? extends UIGrid> getType() {
		return UIObjectTypes.GRID;
	}

	public void setElementWidth(float elementWidth) {
		this.elementWidth = elementWidth;
		placeChilds(0);
	}

	public float getElementWidth() {
		return elementWidth;
	}

	@Override
	protected void placeChild(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, 1 - getWindow().getReservedTopSpace() - (elementHeight * (1 + height)),
				elementWidth, elementHeight);
	}
}