package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.options.Options;
import org.jgine.misc.utils.scheduler.Task;
import org.jgine.misc.utils.script.ScriptManager;
import org.jgine.render.RenderTarget;
import org.jgine.render.Renderer;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;

public class UIWindow extends UICompound {

	Entity entity;
	UIScene scene;
	private boolean moveAble;
	private boolean hide;
	private boolean floating;
	private Material background;
	private Material border;
	ScriptEngine scriptEngine;
	private RenderTarget renderTarget;
	private Material renderTargetMaterial;

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
		this.moveAble = moveAble;
		hide = false;
		floating = false;
		setScale(width, height);
		background = new Material(Color.DARK_GRAY);
		border = new Material(Color.BLACK);
		scriptEngine = ScriptManager.NULL_SCRIPT_ENGINE;
		renderTarget = createRenderTarget();
		renderTargetMaterial = new Material(renderTarget.getTexture(RenderTarget.COLOR_ATTACHMENT0));
		renderTargetMaterial.flipY();
	}

	@Override
	protected void free() {
		renderTarget.close();
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.background = background.clone();
		obj.border = border.clone();
		obj.renderTarget = createRenderTarget();
		obj.renderTargetMaterial = renderTargetMaterial.clone();
		obj.renderTargetMaterial.setTexture(obj.renderTarget.getTexture(RenderTarget.COLOR_ATTACHMENT0));
		return obj;
	}

	@Override
	public void render() {
		if (hide)
			return;
		UIRenderer.renderQuad(getTransform(), background);
		renderChilds();
		UIRenderer.renderLine2d(getTransform(), border, true,
				new float[] { -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f });
	}

	@Override
	protected void renderChilds() {
		RenderTarget tmp = Renderer.getRenderTarget();
		Renderer.setRenderTarget(renderTarget);
		renderTarget.clear();
		for (UIObject child : getVisibleChilds())
			child.render();
		Renderer.setRenderTarget(tmp);
		UIRenderer.renderQuad(getTransform(), renderTargetMaterial);
	}

	@Override
	protected void calculateTransform() {
		calculateTransformBase();
	}

	@Override
	public void updateTransform(Matrix matrix) {
	}

	@Override
	@Nullable
	public UIWindow getWindow() {
		if (parent == null)
			return null;
		return super.getWindow();
	}

	@Override
	public void addChildIntern(UIObject child) {
		super.addChildIntern(child);
		setChildFunctions(child);
	}

	protected void setChildFunctions(UIObject child) {
		child.setScrollFunction((object, scroll) -> addScrollY(scroll.intValue()));
	}

	@Override
	public List<UIObject> getVisibleChilds() {
		// TODO Optimization
		return super.getVisibleChilds();
	}

	@Override
	public void onScroll(float scroll) {
		super.onScroll(scroll);
		addScrollY((int) scroll);
	}

	public void addScrollX(int scroll) {
		float newScroll = renderTargetMaterial.getTextureX() + scroll * 0.01f;
		if (newScroll > 0.0f)
			newScroll = 0.0f;
		else if (newScroll < -getViewWidth() - 1.0f)
			newScroll = -getViewWidth() - 1.0f;
		renderTargetMaterial.setTextureX(newScroll);
	}

	public void addScrollY(int scroll) {
		float newScroll = renderTargetMaterial.getTextureY() + scroll * 0.01f;
		if (newScroll > 0.0f)
			newScroll = 0.0f;
		else if (newScroll < -getViewHeight() - 1.0f)
			newScroll = -getViewHeight() - 1.0f;
		renderTargetMaterial.setTextureY(newScroll);
	}

	public void setScrollX(float scroll) {
		if (scroll > 0.0f)
			scroll = 0.0f;
		else if (scroll < -getViewWidth() - 1.0f)
			scroll = -getViewWidth() - 1.0f;
		renderTargetMaterial.setTextureX(scroll);
	}

	public void setScrollY(float scroll) {
		if (scroll > 0.0f)
			scroll = 0.0f;
		else if (scroll < -getViewHeight() - 1.0f)
			scroll = -getViewHeight() - 1.0f;
		renderTargetMaterial.setTextureY(scroll);
	}

	public float getScrollX() {
		return renderTargetMaterial.getTextureX();
	}

	public float getScrollY() {
		return renderTargetMaterial.getTextureY();
	}

	public void setViewSize(float width, float sizeY) {
		setViewWidth(width);
		setViewHeight(sizeY);
	}

	public Vector2f getViewSize() {
		return new Vector2f(getViewWidth(), getViewHeight());
	}

	public void setViewWidth(float width) {
		renderTargetMaterial.setTextureWidth(width);
	}

	public void setViewHeight(float height) {
		renderTargetMaterial.setTextureHeight(height);
	}

	public float getViewWidth() {
		return renderTargetMaterial.getTextureWidth();
	}

	public float getViewHeight() {
		return renderTargetMaterial.getTextureHeight();
	}

	@Override
	public void setScrollFunction(BiConsumer<UIObject, Float> scrollFunction) {
		super.setScrollFunction(scrollFunction);
		for (UIObject child : getChilds())
			child.setScrollFunction(scrollFunction);
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

		Object scriptName = data.get("script");
		if (scriptName instanceof String) {
			ScriptEngine scriptEngine = ResourceManager.getScript((String) scriptName);
			if (scriptEngine != null)
				this.scriptEngine = scriptEngine;
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
		out.writeUTF(ResourceManager.getScriptName(scriptEngine));
	}

	@Override
	public UIObjectType<? extends UIWindow> getType() {
		return UIObjectTypes.WINDOW;
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

	private static RenderTarget createRenderTarget() {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTexture(Texture.RGBA, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt());
		renderTarget.checkStatus();
		renderTarget.unbind();
		return renderTarget;
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
			float mouseX = window.scene.mouseX;
			float mouseY = window.scene.mouseY;
			float distance = Vector2f.distance(mouseX, mouseY, dragX, dragY);
			if (distance > 0.01f) {
				float newX = window.getX() + (mouseX - dragX);
				float newY = window.getY() + (mouseY - dragY);
				window.setPos(newX, newY);
				dragX = mouseX;
				dragY = mouseY;
			}
		}
	}
}
