package jgine.system.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.system.ui.UIObject;
import jgine.system.ui.UIObjectType;
import jgine.utils.ObjectUtils;
import jgine.utils.math.FastMath;

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
		elementWidth = ObjectUtils.toFloat(data.get("elementWidth"), elementWidth);
		super.load(data);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("elementWidth", elementWidth);
		super.save(data);
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
		return UIObjectType.GRID;
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
		child.setPos(widthSize - height, 1 - (elementHeight * (1 + height - scroll)));
		child.setScale(elementWidth, elementHeight);
	}

	@Override
	public void placeChildReverse(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.setPos(widthSize - height, elementHeight * (height - scroll));
		child.setScale(elementWidth, elementHeight);
	}
}