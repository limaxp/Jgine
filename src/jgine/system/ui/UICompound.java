package jgine.system.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgine.core.Entity;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.math.Matrix;
import jgine.utils.registry.Registry;

public class UICompound extends UIObject {

	Entity entity;
	List<UIObject> childs;

	public UICompound() {
		childs = new UnorderedIdentityArrayList<UIObject>();
	}

	@Override
	protected void free() {
		for (UIObject child : childs)
			child.free();
		childs = null;
	}

	@Override
	public void render() {
		for (UIObject child : getVisibleChilds())
			child.render();
	}

	@Override
	public UICompound clone() {
		UICompound obj = (UICompound) super.clone();
		obj.childs = new UnorderedIdentityArrayList<UIObject>();
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

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		int size = childs.size();
		@SuppressWarnings("unchecked")
		Map<String, Object>[] result = new Map[size];
		for (int i = 0; i < size; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			result[i] = map;
			childs.get(i).save(map);
		}
		data.put("childs", Arrays.asList(result));
	}

	private void loadChild(Object data) {
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> childData = (Map<String, Object>) data;
			UIObjectType<?> uiObjectType;
			Object type = childData.get("type");
			if (type instanceof String) {
				uiObjectType = Registry.UI_OBJECT.get((String) type);
				if (uiObjectType == null)
					uiObjectType = UIObjectType.LABEL;
			} else
				uiObjectType = UIObjectType.LABEL;
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
			UIObject object = Registry.UI_OBJECT.get(in.readInt()).get();
			addChildIntern(object);
			object.load(in);
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeInt(childs.size());
		for (UIObject child : childs) {
			out.writeInt(child.getType().id());
			child.save(out);
		}
	}

	@Override
	public UIObjectType<? extends UICompound> getType() {
		return UIObjectType.COMPOUND;
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

	@Override
	public Entity getEntity() {
		return entity;
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
		removeChild(index);
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
		return Collections.unmodifiableList(childs);
	}

	public List<UIObject> getVisibleChilds() {
		return getChilds();
	}
}
