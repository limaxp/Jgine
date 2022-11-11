package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.misc.utils.script.ScriptManager;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;

public class UIWindow extends UIObject {

	Entity entity;
	UIScene scene;
	List<UIObject> childs;
	private List<UIObject> childsView;
	private boolean moveAble;
	private boolean hide;
	private Material background;
	private Vector3f borderColor;
	ScriptEngine scriptEngine;

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
		childsView = Collections.unmodifiableList(childs);
		this.moveAble = moveAble;
		hide = false;
		setScale(width, height);
		background = new Material(Color.TRANSLUCENT_WEAK);
		borderColor = Vector3f.NULL;
		scriptEngine = ScriptManager.NULL_SCRIPT_ENGINE;
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.childs = new UnorderedIdentityArrayList<UIObject>();
		obj.childsView = Collections.unmodifiableList(obj.childs);
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
		if (focusFunction != null)
			ScriptManager.invoke(scriptEngine, focusFunction, this);
	}

	@Override
	public void onDefocus() {
		if (defocusFunction != null)
			ScriptManager.invoke(scriptEngine, defocusFunction, this);
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
		if (clickFunction != null)
			ScriptManager.invoke(scriptEngine, clickFunction, this);
	}

	@Override
	public void onRelease(float mouseX, float mouseY) {
		if (releaseFunction != null)
			ScriptManager.invoke(scriptEngine, releaseFunction, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object moveAbleData = data.get("moveAble");
		if (moveAbleData != null)
			moveAble = YamlHelper.toBoolean(moveAbleData);
		Object hideData = data.get("hide");
		if (hideData != null)
			hide = YamlHelper.toBoolean(hideData);
		Object backgroundData = data.get("background");
		if (backgroundData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) backgroundData);
			if (texture != null)
				background.setTexture(texture);
		} else if (backgroundData instanceof Map)
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

		Object scriptName = data.get("script");
		if (scriptName instanceof String) {
			ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
			if (scriptEngine != null)
				this.scriptEngine = scriptEngine;
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
		ScriptEngine loadedScript = ResourceManager.getScript(in.readUTF());
		if (loadedScript != null)
			scriptEngine = loadedScript;
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
		out.writeUTF(ResourceManager.getScriptName(scriptEngine));
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

	@Override
	public void setWindow(@Nullable UIWindow window) {
		super.setWindow(window);
		this.entity = window.entity;
		this.scene = window.scene;
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
		return childsView;
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

	public Entity getEntity() {
		return entity;
	}

	public UIScene getScene() {
		return scene;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public ScriptEngine getScriptEngine() {
		return scriptEngine;
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
