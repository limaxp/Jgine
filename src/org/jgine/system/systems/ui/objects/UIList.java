package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jgine.system.systems.ui.UICompound;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.FastMath;

public class UIList extends UICompound {

	protected float elementHeight = 0.05f;
	protected boolean reverse;
	protected int scroll;
	protected boolean scrollable;

	public UIList() {
		this(0.5f);
	}

	public UIList(float scale) {
		this(scale, scale);
	}

	public UIList(float width, float height) {
		setScale(width, height);
	}

	@Override
	public List<UIObject> getVisibleChilds() {
		List<UIObject> childs = getChilds();
		int index = getScrollIndex();
		return childs.subList(index, index + FastMath.min(childs.size() - index, getMaxElements()));
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
	public void addChildIntern(UIObject child) {
		super.addChildIntern(child);
		int index = getChilds().size() - 1;
		int scroll = getScrollIndex();
		if (index >= scroll && index <= scroll + getMaxElements()) {
			if (reverse)
				placeChildReverse(child, index);
			else
				placeChild(child, index);
		}
		setChildFunctions(child);
	}

	protected void setChildFunctions(UIObject child) {
		child.setClickFunction(getClickFunction());
		child.setReleaseFunction(getReleaseFunction());
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

	@Override
	public void onClick(int key) {
	}

	@Override
	public void onRelease(int key) {
	}

	@Override
	public void setClickFunction(BiConsumer<UIObject, Integer> clickFunction) {
		super.setClickFunction(clickFunction);
		for (UIObject child : getChilds())
			child.setClickFunction(clickFunction);
	}

	@Override
	public void setReleaseFunction(BiConsumer<UIObject, Integer> releaseFunction) {
		super.setReleaseFunction(releaseFunction);
		for (UIObject child : getChilds())
			child.setReleaseFunction(releaseFunction);
	}

	@Override
	public void setScrollFunction(BiConsumer<UIObject, Float> scrollFunction) {
		super.setScrollFunction(scrollFunction);
		for (UIObject child : getChilds())
			child.setScrollFunction(scrollFunction);
	}

	public void setElementHeight(float elementHeight) {
		this.elementHeight = elementHeight;
		placeChilds(0);
	}

	public float getElementHeight() {
		return elementHeight;
	}

	public int getMaxElements() {
		return (int) (1.0f / elementHeight);
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

	public int getScrollIndex() {
		return scroll;
	}

	public int getIndex(UIObject child) {
		return getIndex(child.getX(), child.getY());
	}

	public int getIndex(float x, float y) {
		int maxElements = (int) (1.0f / elementHeight);
		int index = maxElements - 1 - FastMath.round(y / elementHeight);
		return scroll + index;
	}

	public void placeChilds(int index) {
		List<UIObject> childs = getVisibleChilds();
		int scroll = getScrollIndex();
		int overflow = FastMath.max(0, index - scroll);
		if (reverse)
			for (int i = overflow; i < childs.size(); i++)
				placeChildReverse(childs.get(i), scroll + i);
		else
			for (int i = overflow; i < childs.size(); i++)
				placeChild(childs.get(i), scroll + i);
	}

	public void placeChild(UIObject child, int index) {
		child.set(0, 1 - (elementHeight * (index + 1 - scroll)), 1, elementHeight);
	}

	public void placeChildReverse(UIObject child, int index) {
		child.set(0, elementHeight * (index - scroll), 1, elementHeight);
	}
}
