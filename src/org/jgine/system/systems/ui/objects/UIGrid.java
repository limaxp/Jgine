package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.FastMath;

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
	public void setScroll(int scroll) {
		if (!scrollable)
			return;
		scroll = this.scroll - scroll;
		if (scroll < 0)
			return;
		int maxY = (int) (1.0f / elementHeight);
		if (scroll + maxY > 1 + getChilds().size() / maxY)
			return;
		this.scroll = scroll;
		placeChilds(0);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object elementWidthData = data.get("elementWidth");
		if (elementWidthData != null)
			elementWidth = YamlHelper.toFloat(elementWidthData, 0.02f);
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
	public int getMaxElements() {
		return (int) (1.0f / elementWidth) * (int) (1.0f / elementHeight);
	}

	@Override
	public int getScrollIndex() {
		int maxX = (int) (1.0f / elementWidth);
		return scroll * maxX;
	}

	@Override
	public int getIndex(float x, float y) {
		int maxX = (int) (1.0f / elementWidth);
		int maxY = (int) (1.0f / elementHeight);
		int xIndex = FastMath.round(x / elementWidth);
		int yIndex = maxY - 1 - FastMath.round(y / elementHeight);
		return scroll * maxX + xIndex + yIndex * maxX;
	}

	@Override
	public void placeChild(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, 1 - (elementHeight * (1 + height - scroll)), elementWidth, elementHeight);
	}

	@Override
	public void placeChildReverse(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, elementHeight * (height - scroll), elementWidth, elementHeight);
	}
}