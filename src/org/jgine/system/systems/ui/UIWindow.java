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
import org.jgine.render.RenderTarget;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.system.systems.script.Script;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.options.Options;
import org.jgine.utils.scheduler.Task;
import org.jgine.utils.script.ScriptManager;

public class UIWindow extends UICompound {

	Entity entity;
	UIScene scene;
	private boolean moveAble;
	private boolean hide;
	private boolean floating;
	private Material background;
	private Material border;
	private float borderSize;
	private Object scriptEngine;
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
		background = new Material(BACKGROUND_COLOR);
		border = new Material(BORDER_COLOR);
		borderSize = 0.005f;
		scriptEngine = ScriptManager.NULL_SCRIPT_ENGINE;
		renderTargetMaterial = new Material();
		renderTargetMaterial.flipY();
	}

	@Override
	protected void free() {
		if (renderTarget != null)
			renderTarget.close();
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.background = background.clone();
		obj.border = border.clone();
		obj.renderTargetMaterial = renderTargetMaterial.clone();
		return obj;
	}

	@Override
	public void render(int depth) {
		if (this.scriptEngine instanceof UIScript)
			((UIScript) this.scriptEngine).onUpdate(this);
		if (hide)
			return;

		renderChilds(depth);
		UIRenderer.renderQuad(getTransform(), renderTargetMaterial, depth);
	}

	@Override
	protected void renderChilds(int depth) {
		RenderTarget tmp = UIRenderer.getRenderTarget();
		RenderTarget renderTarget = getRenderTarget();
		UIRenderer.setRenderTarget(renderTarget);
		renderTarget.clear();
		UIRenderer.renderQuad(new Matrix(), border, 0);
		UIRenderer.renderQuad(new Matrix().scale(1.0f - borderSize, 1.0f - borderSize, 1.0f - borderSize), background,
				0);
		for (UIObject child : getVisibleChilds())
			child.render(0);
		UIRenderer.setRenderTarget(tmp);
	}

	private RenderTarget getRenderTarget() {
		if (renderTarget == null) {
			renderTarget = createRenderTarget();
			renderTargetMaterial.setTexture(renderTarget.getTexture(RenderTarget.COLOR_ATTACHMENT0));
		}
		return renderTarget;
	}

	@Override
	protected void calculateTransform() {
		calculateTransformBase();
	}

	@Override
	public void updateTransform(Matrix matrix) {
		matrix.scale(1.0f - borderSize, 1.0f - borderSize, 1.0f - borderSize);
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
		if (child.getScrollFunction() == NULL_VALUE_FUNCTION)
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

		Object borderSizeData = data.get("borderSize");
		if (borderSizeData != null)
			borderSize = YamlHelper.toFloat(borderSizeData);

		Object scriptName = data.get("script");
		if (scriptName instanceof String) {
			Script script = Script.get((String) scriptName);
			if (script != null)
				this.scriptEngine = script;
			else
				this.scriptEngine = ResourceManager.getScript((String) scriptName);
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
		borderSize = in.readFloat();
		String scriptName = in.readUTF();
		Script script = Script.get(scriptName);
		if (script != null)
			scriptEngine = script;
		else
			scriptEngine = ResourceManager.getScript(scriptName);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeBoolean(moveAble);
		out.writeBoolean(hide);
		out.writeBoolean(floating);
		background.save(out);
		border.save(out);
		out.writeFloat(borderSize);
		if (scriptEngine != null) {
			if (scriptEngine instanceof Script)
				out.writeUTF(scriptEngine.getClass().getSimpleName());
			else
				out.writeUTF(ResourceManager.getScriptName((ScriptEngine) scriptEngine));
		} else
			out.writeUTF("");
	}

	@Override
	public UIObjectType<? extends UIWindow> getType() {
		return UIObjectTypes.WINDOW;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		if (this.scriptEngine instanceof UIScript)
			((UIScript) this.scriptEngine).onEnable(this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (this.scriptEngine instanceof UIScript)
			((UIScript) this.scriptEngine).onDisable(this);
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

	public void setBorderSize(float borderSize) {
		this.borderSize = borderSize;
	}

	public float getBorderSize() {
		return borderSize;
	}

	public Entity getEntity() {
		return entity;
	}

	public UIScene getScene() {
		return scene;
	}

	public void setScript(ScriptEngine script) {
		this.scriptEngine = script;
	}

	public void setScript(Script script) {
		this.scriptEngine = script;
	}

	@Override
	public Object getScriptEngine() {
		return scriptEngine;
	}

	private static RenderTarget createRenderTarget() {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTexture(Texture.RGBA, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt());
		renderTarget.setRenderBuffer(RenderTarget.DEPTH24_STENCIL8, RenderTarget.DEPTH_STENCIL_ATTACHMENT,
				Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt());
		renderTarget.checkStatus();
		RenderTarget.unbind();
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
