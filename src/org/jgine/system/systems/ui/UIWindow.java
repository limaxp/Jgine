package org.jgine.system.systems.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;

public class UIWindow extends UIObject {

	UIScene scene;
	List<UIObject> childs;
	private boolean moveAble;
	private Material background;
	private Vector3f borderColor;

	public UIWindow() {
		this(RASTER_SIZE, false);
	}

	public UIWindow(int scale) {
		this(scale, false);
	}

	public UIWindow(int scale, boolean moveAble) {
		this(scale, scale, moveAble);
	}
	
	public UIWindow(int width, int height) {
		this(width, height, false);
	}

	public UIWindow(int width, int height, boolean moveAble) {
		childs = new UnorderedIdentityArrayList<UIObject>();
		this.moveAble = moveAble;
		setScale(width, height);
		background = new Material(new Vector4f(1, 1, 1, 0.2f));
		borderColor = Vector3f.NULL;
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.childs = new UnorderedIdentityArrayList<UIObject>();
		for (UIObject child : childs) {
			UIObject cloneChild = child.clone();
			cloneChild.window = obj;
			obj.childs.add(cloneChild);
		}
		obj.background = background.clone();
		return obj;
	}

	@Override
	protected void create(Entity entity) {
		for (UIObject child : childs)
			scene.initObject(entity, child);
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		UIRenderer.renderLine(getTransform(), new float[] { -1, -1, 0, 1, -1, 0, 1, 1, 0, -1, 1, 0 },
				new Material(borderColor), true);
	}

	@Override
	public void onFocus() {
	}

	@Override
	public void onDefocus() {
	}

	@Override
	public void onClick(int mouseX, int mouseY) {
	}

	@Override
	public void onRelease(int mouseX, int mouseY) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);

		Object moveAble = data.get("moveAble");
		if (moveAble != null && moveAble instanceof Boolean)
			this.moveAble = (boolean) moveAble;

		Object childs = data.get("childs");
		if (childs != null && childs instanceof Map) {
			Map<String, Object> childMap = (Map<String, Object>) childs;
			for (Object subData : childMap.values()) {
				if (subData instanceof Map) {
					Map<String, Object> childData = (Map<String, Object>) subData;
					UIObjectType<?> uiObjectType;
					Object type = childData.get("type");
					if (type != null && type instanceof String) {
						uiObjectType = UIObjectTypes.get((String) type);
						if (uiObjectType == null)
							uiObjectType = UIObjectTypes.LABEL;
					} else
						uiObjectType = UIObjectTypes.LABEL;
					UIObject uiObject = uiObjectType.get();
					uiObject.load(childData);
					addChild(uiObject);
				}
			}
		}
	}

	@Override
	public UIObjectType<? extends UIWindow> getType() {
		return UIObjectTypes.WINDOW;
	}

	@Override
	protected Matrix calculateTransform() {
		Matrix transform = super.calculateTransform();
		for (UIObject child : childs)
			child.transformChanged = true;
		return transform;
	}

	public final <T extends UIObject> T addChild(T child) {
		if (child.window != null)
			child.window.childs.remove(this);
		child.window = this;
		childs.add(child);
		return child;
	}

	public final void removeChild(UIObject child) {
		child.window = null;
		childs.remove(child);
	}

	public final void isChild(UIObject child) {
		childs.contains(child);
	}

	public final void addChilds(Collection<UIObject> childs) {
		for (UIObject child : childs)
			addChild(child);
	}

	public final void removeChilds(Collection<UIObject> childs) {
		for (UIObject child : childs)
			removeChild(child);
	}

	public final void clearChilds() {
		for (UIObject child : childs)
			child.window = null;
		childs.clear();
	}

	public final void setChilds(Collection<UIObject> childs) {
		if (!childs.isEmpty())
			clearChilds();
		for (UIObject child : childs)
			addChild(child);
	}

	public final List<UIObject> getChilds() {
		return Collections.unmodifiableList(childs);
	}

	public void setMoveAble(boolean moveAble) {
		this.moveAble = moveAble;
	}

	public boolean isMoveAble() {
		return moveAble;
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

	public static class DragTask implements Runnable {

		private UIWindow window;
		private int dragX, dragY;
		private boolean isCanceled;

		public DragTask(UIWindow window) {
			this.window = window;
			this.dragX = window.scene.mouseX;
			this.dragY = window.scene.mouseY;
		}

		@Override
		public void run() {
			int newX = window.getX() + (window.scene.mouseX - dragX);
			int newY = window.getY() + (window.scene.mouseY - dragY);
			window.setPos(newX, newY);
			dragX = window.scene.mouseX;
			dragY = window.scene.mouseY;
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
