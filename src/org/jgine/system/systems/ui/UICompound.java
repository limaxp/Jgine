package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgine.collection.list.UnorderedIdentityArrayList;
import org.jgine.utils.math.Matrix;

public class UICompound extends UIObject {

	List<UIObject> childs;
	private List<UIObject> childsView;

	public UICompound() {
		childs = new UnorderedIdentityArrayList<UIObject>();
		childsView = Collections.unmodifiableList(childs);
	}

	@Override
	protected void free() {
	}

	@Override
	public void render(int depth) {
		renderChilds(depth + 1);
	}

	protected void renderChilds(int depth) {
		for (UIObject child : getVisibleChilds())
			child.render(depth);
	}

	@Override
	public UICompound clone() {
		UICompound obj = (UICompound) super.clone();
		obj.childs = new UnorderedIdentityArrayList<UIObject>();
		obj.childsView = Collections.unmodifiableList(obj.childs);
		for (UIObject child : childs)
			obj.addChild(child.clone());
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object childs = data.get("childs");
		if (childs instanceof List) {
			List<Object> childList = (List<Object>) childs;
			for (Object subData : childList)
				loadChild(subData);
		} else if (childs instanceof Map) {
			Map<String, Object> childMap = (Map<String, Object>) childs;
			for (Object subData : childMap.values())
				loadChild(subData);
		}
	}

	private void loadChild(Object data) {
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> childData = (Map<String, Object>) data;
			UIObjectType<?> uiObjectType;
			Object type = childData.get("type");
			if (type instanceof String) {
				uiObjectType = UIObjectTypes.get((String) type);
				if (uiObjectType == null)
					uiObjectType = UIObjectTypes.LABEL;
			} else
				uiObjectType = UIObjectTypes.LABEL;
			UIObject object = uiObjectType.get();
			addChildIntern(object);
			object.load(childData);
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		int childSize = in.readInt();
		for (int i = 0; i < childSize; i++) {
			UIObject object = UIObjectTypes.get(in.readInt()).get();
			addChildIntern(object);
			object.load(in);
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeInt(childs.size());
		for (UIObject child : childs) {
			out.writeInt(child.getType().getId());
			child.save(out);
		}
	}

	@Override
	public UIObjectType<? extends UICompound> getType() {
		return UIObjectTypes.COMPOUND;
	}

	@Override
	protected void calculateTransform() {
		super.calculateTransform();
		for (UIObject child : childs)
			child.calculateTransform();
	}

	protected final void calculateTransformBase() {
		super.calculateTransform();
	}

	public void updateTransform(Matrix transform) {
		transform.mult(getTransform());
	}

	public void addChild(UIObject child) {
		addChildIntern(child);
		child.calculateTransform();
	}

	protected void addChildIntern(UIObject child) {
		childs.add(child);
		child.parent = this;
	}

	public int removeChild(UIObject child) {
		int index = childs.indexOf(child);
		childs.remove(index);
		child.onDisable();
		child.free();
		return index;
	}

	public UIObject removeChild(int index) {
		UIObject child = childs.remove(index);
		child.onDisable();
		child.free();
		return child;
	}

	public void isChild(UIObject child) {
		childs.contains(child);
	}

	public final void addChilds(Collection<UIObject> childs) {
		for (UIObject child : childs)
			addChild(child);
	}

	public void removeChilds(Collection<UIObject> childs) {
		for (UIObject child : childs)
			removeChild(child);
	}

	public void clearChilds() {
		for (UIObject child : childs) {
			child.onDisable();
			child.free();
		}
		childs.clear();
	}

	public void setChilds(Collection<UIObject> childs) {
		if (!childs.isEmpty())
			clearChilds();
		for (UIObject child : childs)
			addChild(child);
	}

	public List<UIObject> getChilds() {
		return childsView;
	}

	public List<UIObject> getVisibleChilds() {
		return getChilds();
	}
}
