package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;

public class UIWindow extends UIObject {

	UIScene scene;
	List<UIObject> childs;
	private boolean moveAble;
	private boolean hide;
	private Material background;
	private Vector3f borderColor;
	private float reservedTopSpace;

	public UIWindow() {
		this(0.5f, false);
	}

	public UIWindow(float scale) {
		this(scale, false);
	}

	public UIWindow(float scale, boolean moveAble) {
		this(scale, scale, moveAble);
	}

	public UIWindow(float width, float height) {
		this(width, height, false);
	}

	public UIWindow(float width, float height, boolean moveAble) {
		childs = new UnorderedIdentityArrayList<UIObject>();
		this.moveAble = moveAble;
		hide = false;
		setScale(width, height);
		background = new Material(Color.TRANSLUCENT_WEAK);
		borderColor = Vector3f.NULL;
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.childs = new UnorderedIdentityArrayList<UIObject>();
		for (UIObject child : childs)
			obj.addChild(child.clone());
		obj.background = background.clone();
		return obj;
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
		if (hide)
			return;
		UIRenderer.renderQuad(getTransform(), background);
		UIRenderer.renderLine2d(getTransform(), new float[] { -1, -1, 1, -1, 1, 1, -1, 1 }, new Material(borderColor),
				true);
		for (UIObject child : childs)
			child.render();
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

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		moveAble = YamlHelper.toBoolean(data.get("moveAble"));
		hide = YamlHelper.toBoolean(data.get("hide"));
		Object backgroundData = data.get("background");
		if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);

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
			if (type != null && type instanceof String) {
				uiObjectType = UIObjectTypes.get((String) type);
				if (uiObjectType == null)
					uiObjectType = UIObjectTypes.LABEL;
			} else
				uiObjectType = UIObjectTypes.LABEL;
			UIObject object = uiObjectType.get();
			object.load(childData);
			addChild(object);
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		moveAble = in.readBoolean();
		hide = in.readBoolean();
		background.load(in);
		int childSize = in.readInt();
		for (int i = 0; i < childSize; i++) {
			UIObject object = UIObjectTypes.get(in.readInt()).get();
			object.load(in);
			addChild(object);
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeBoolean(moveAble);
		out.writeBoolean(hide);
		background.save(out);
		out.writeInt(childs.size());
		for (UIObject child : childs) {
			out.writeInt(child.getType().getId());
			child.save(out);
		}
	}

	@Override
	public UIObjectType<? extends UIWindow> getType() {
		return UIObjectTypes.WINDOW;
	}

	@Override
	protected void calculateTransform() {
		super.calculateTransform();
		for (UIObject child : childs)
			child.calculateTransform();
	}

	public void addChild(UIObject child) {
		child.setWindow(this);
	}

	public int removeChild(UIObject child) {
		int index = childs.indexOf(child);
		childs.remove(index);
		child.window = null;
		child.calculateTransform();
		return index;
	}

	public UIObject removeChild(int index) {
		UIObject child = childs.remove(index);
		child.window = null;
		child.calculateTransform();
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
			child.window = null;
			child.calculateTransform();
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

	public void setMoveAble(boolean moveAble) {
		this.moveAble = moveAble;
	}

	public boolean isMoveAble() {
		return moveAble;
	}

	public void show() {
		this.hide = false;
	}

	public void hide() {
		this.hide = true;
	}

	public void setHidden(boolean hide) {
		this.hide = hide;
	}

	public boolean isHidden() {
		return hide;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setBorderColor(Vector3f borderColor) {
		this.borderColor = borderColor;
	}

	public Vector3f getBorderColor() {
		return borderColor;
	}

	public UIScene getScene() {
		return scene;
	}

	public void setReservedTopSpace(float reservedTopSpace) {
		this.reservedTopSpace = reservedTopSpace;
	}

	public void addReservedTopSpace(float reservedTopSpace) {
		this.reservedTopSpace += reservedTopSpace;
	}

	public float getReservedTopSpace() {
		return reservedTopSpace;
	}

	public static class DragTask implements Runnable {

		private UIWindow window;
		private float dragX, dragY;
		private boolean isCanceled;

		public DragTask(UIWindow window) {
			this.window = window;
			this.dragX = window.scene.mouseX;
			this.dragY = window.scene.mouseY;
		}

		@Override
		public void run() {
			float distance = Vector2f.distance(window.scene.mouseX, window.scene.mouseY, dragX, dragY);
			if (distance > 0.01f) {
				float newX = window.getX() + (window.scene.mouseX - dragX);
				float newY = window.getY() + (window.scene.mouseY - dragY);
				window.setPos(newX, newY);
				dragX = window.scene.mouseX;
				dragY = window.scene.mouseY;
			}
			if (!isCanceled)
				Scheduler.runTaskAsynchron(this);
		}

		public void cancel() {
			isCanceled = true;
		}

		public boolean isCanceled() {
			return isCanceled;
		}
	}
}
