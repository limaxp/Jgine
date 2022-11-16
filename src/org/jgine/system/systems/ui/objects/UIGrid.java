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
	protected void renderChilds() {
		List<UIObject> childs = getChilds();
		int size = childs.size();
		int maxX = (int) (1.0f / elementWidth);
		int maxY = (int) (1.0f / elementHeight);

		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				int index = x + ((y + scroll) * maxX);
				if (index >= size)
					break;
				childs.get(index).render();
			}
		}
	}

	@Override
	public void setScroll(int scroll) {
		if (!scrollable)
			return;
		this.scroll -= scroll;
		if (this.scroll < 0)
			this.scroll = 0;
		int maxY = (int) (1.0f / elementHeight);
		int size = 1 + getChilds().size() / maxY;
		if (this.scroll > size)
			this.scroll = size;
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
	protected void placeChild(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, 1 - (elementHeight * (1 + height - scroll)), elementWidth, elementHeight);
	}

	@Override
	protected void placeChildReverse(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.set(widthSize - height, elementHeight * (height - scroll), elementWidth, elementHeight);
	}
}