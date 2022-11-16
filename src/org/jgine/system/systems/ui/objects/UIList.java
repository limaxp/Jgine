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
	protected boolean reverse;
	protected int scroll;
	protected boolean scrollable;

	public UIList() {
	}

	public UIList(float scale) {
		super(scale);
	}

	public UIList(float width, float height) {
		super(width, height);
	}

	@Override
	protected void renderChilds() {
		List<UIObject> childs = getChilds();
		int size = childs.size();
		int maxElements = (int) (1.0f / elementHeight);
		for (int i = 0; i < maxElements; i++) {
			int j = i + scroll;
			if (j >= size)
				break;
			childs.get(j).render();
		}
	}

	@Override
	public void onScroll(float scroll) {
		super.onScroll(scroll);
		setScroll((int) scroll);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object elementHeightData = data.get("elementHeight");
		if (elementHeightData != null)
			this.elementHeight = YamlHelper.toFloat(elementHeightData, 0.05f);
		Object reverseData = data.get("reverse");
		if (reverseData != null)
			this.reverse = YamlHelper.toBoolean(reverseData);
		Object scrollableData = data.get("scrollable");
		if (scrollableData != null)
			this.scrollable = YamlHelper.toBoolean(scrollableData);
		super.load(data);
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.elementHeight = in.readFloat();
		this.reverse = in.readBoolean();
		this.scrollable = in.readBoolean();
		super.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(elementHeight);
		out.writeBoolean(reverse);
		out.writeBoolean(scrollable);
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
		if (reverse)
			placeChildReverse(child, size - 1);
		else
			placeChild(child, size - 1);
		child.setScrollFunction((object, scroll) -> setScroll(scroll.intValue()));
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

	public void setReverse(boolean reversed) {
		this.reverse = reversed;
		placeChilds(0);
	}

	public boolean isReversed() {
		return reverse;
	}

	public void setScroll(int scroll) {
		if (!scrollable)
			return;
		scroll = this.scroll - scroll;
		if (scroll < 0)
			return;
		int maxY = (int) (1.0f / elementHeight);
		if (scroll + maxY > getChilds().size())
			return;
		this.scroll = scroll;
		placeChilds(0);
	}

	public int getScroll() {
		return scroll;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	protected void placeChilds(int index) {
		List<UIObject> childs = getChilds();
		int size = childs.size();
		if (reverse)
			for (int i = index; i < size; i++)
				placeChildReverse(childs.get(i), i);
		else
			for (int i = index; i < size; i++)
				placeChild(childs.get(i), i);
	}

	protected void placeChild(UIObject child, int index) {
		child.set(0, 1 - (elementHeight * (index + 1 - scroll)), 1, elementHeight);
	}

	protected void placeChildReverse(UIObject child, int index) {
		child.set(0, elementHeight * (index - scroll), 1, elementHeight);
	}
}
