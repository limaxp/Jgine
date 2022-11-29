package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.scheduler.Task;
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
	private boolean floating;
	private Material background;
	private Material border;
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
		floating = false;
		setScale(width, height);
		background = new Material(Color.TRANSLUCENT_WEAK);
		border = new Material(Color.BLACK);
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
		renderChilds();
		UIRenderer.renderLine2d(getTransform(), new float[] { -1, -1, 1, -1, 1, 1, -1, 1 }, border, true);
	}

	protected void renderChilds() {
		for (UIObject child : getVisibleChilds())
			child.render();
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
		Object floatingData = data.get("floating");
		if (floatingData != null)
			floating = YamlHelper.toBoolean(floatingData);

		Object backgroundData = data.get("background");
		if (backgroundData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) backgroundData);
			if (texture != null)
				background.setTexture(texture);
		} else if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);

		Object borderData = data.get("border");
		if (borderData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) borderData);
			if (texture != null)
				border.setTexture(texture);
		} else if (borderData instanceof Map)
			border.load((Map<String, Object>) borderData);

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
		floating = in.readBoolean();
		background.load(in);
		border.load(in);
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
		out.writeBoolean(floating);
		background.save(out);
		border.save(out);
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

	public void addChild(UIObject child) {
		child.window = this;
		childs.add(child);
		child.calculateTransform();
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

	public void setFloating(boolean floating) {
		this.floating = floating;
	}

	public boolean isFloating() {
		return floating;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setBorder(Material border) {
		this.border = border;
	}

	public Material getBorder() {
		return border;
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

	@Override
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public static class DragTask extends Task {

		private UIWindow window;
		private float dragX, dragY;

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
		}
	}
}
