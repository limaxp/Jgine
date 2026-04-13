package jgine.system.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.script.ScriptEngine;

import jgine.core.input.Input;
import jgine.render.RenderTarget;
import jgine.render.UIRenderer;
import jgine.render.material.Material;
import jgine.render.material.Texture;
import jgine.system.script.ScriptBase;
import jgine.system.script.ScriptManager;
import jgine.utils.ObjectUtils;
import jgine.utils.Options;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector2i;
import jgine.utils.scheduler.Scheduler;
import jgine.utils.scheduler.TaskBuffer.Task;

public class UIWindow extends UICompound {

	private String name;
	private boolean moveAble;
	private boolean hide;
	private boolean floating;
	private Material background;
	private Material border;
	private float borderSize;
	private Object scriptEngine;
	private RenderTarget renderTarget;
	Material renderTargetMaterial;
	private Consumer<UIObject> updateFunction;

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
		name = getType().name;
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
		updateFunction = NULL_FUNCTION;
	}

	@Override
	protected void free() {
		super.free();
		if (renderTarget != null)
			Scheduler.runTask(renderTarget::close);
	}

	@Override
	public UIWindow clone() {
		UIWindow obj = (UIWindow) super.clone();
		obj.background = background.clone();
		obj.border = border.clone();
		obj.renderTargetMaterial = renderTargetMaterial.clone();
		return obj;
	}

	public void onUpdate() {
		updateFunction.accept(this);
	}

	void preRender() {
		onUpdate();
		if (hide)
			return;
		renderChilds();
	}

	@Override
	public void render() {
		preRender();
		UIRenderer.renderQuad(getTransform(), renderTargetMaterial);
	}

	private void renderChilds() {
		RenderTarget tmp = UIRenderer.getRenderTarget();
		RenderTarget renderTarget = getRenderTarget();
		UIRenderer.setRenderTarget(renderTarget);
		renderTarget.clear();
		UIRenderer.renderQuad(new Matrix(), border);
		UIRenderer.renderQuad(new Matrix().scale(1.0f - borderSize, 1.0f - borderSize, 1.0f - borderSize), background);
		for (UIObject child : getVisibleChilds())
			child.render();
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
	public void addChildIntern(UIObject child) {
		super.addChildIntern(child);
		setChildFunctions(child);
	}

	protected void setChildFunctions(UIObject child) {
		if (child.getScrollFunction() == NULL_VALUE_FUNCTION)
			child.setScrollFunction((_, scroll) -> addScrollY(scroll.intValue()));
	}

	@Override
	public List<UIObject> getVisibleChilds() {
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

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		name = ObjectUtils.toString(data.get("name"), name);
		moveAble = ObjectUtils.toBoolean(data.get("moveAble"), moveAble);
		hide = ObjectUtils.toBoolean(data.get("hide"), hide);
		floating = ObjectUtils.toBoolean(data.get("floating"), floating);
		ObjectUtils.toMaterial(background, data.get("background"));
		ObjectUtils.toMaterial(border, data.get("border"));
		borderSize = ObjectUtils.toFloat(data.get("borderSize"), borderSize);

		Object scriptName = data.get("script");
		if (scriptName instanceof String) {
			ScriptBase script = ScriptBase.get((String) scriptName);
			if (script != null)
				this.scriptEngine = script;
			else
				this.scriptEngine = ResourceManager.getScript((String) scriptName);
		}
		Object updateFunctionData = data.get("onUpdate");
		if (updateFunctionData != null)
			updateFunction = new ScriptFunction(ObjectUtils.toString(updateFunctionData));
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		data.put("name", name);
		data.put("moveAble", moveAble ? "true" : "false");
		data.put("hide", hide ? "true" : "false");
		data.put("floating", floating ? "true" : "false");
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
		Map<String, Object> borderMap = new HashMap<String, Object>();
		border.save(borderMap);
		data.put("border", borderMap);
		data.put("borderSize", borderSize);
		data.put("script", getScriptName());
		data.put("onUpdate", getFunctionName(updateFunction));
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		name = in.readUTF();
		moveAble = in.readBoolean();
		hide = in.readBoolean();
		floating = in.readBoolean();
		background.load(in);
		border.load(in);
		borderSize = in.readFloat();
		String scriptName = in.readUTF();
		ScriptBase script = ScriptBase.get(scriptName);
		if (script != null)
			scriptEngine = script;
		else
			scriptEngine = ResourceManager.getScript(scriptName);
		updateFunction = loadFunction(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeUTF(name);
		out.writeBoolean(moveAble);
		out.writeBoolean(hide);
		out.writeBoolean(floating);
		background.save(out);
		border.save(out);
		out.writeFloat(borderSize);
		out.writeUTF(getScriptName());
		out.writeUTF(getFunctionName(updateFunction));
	}

	@Override
	public UIObjectType<? extends UIWindow> getType() {
		return UIObjectType.WINDOW;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

	public void setScript(ScriptEngine script) {
		this.scriptEngine = script;
	}

	public void setScript(ScriptBase script) {
		this.scriptEngine = script;
	}

	@Override
	public UIWindow getWindow() {
		return this;
	}

	public Object getScriptEngine() {
		return scriptEngine;
	}

	public String getScriptName() {
		if (scriptEngine instanceof ScriptBase)
			return scriptEngine.getClass().getSimpleName();
		else if (scriptEngine instanceof ScriptEngine)
			return ResourceManager.getScriptName((ScriptEngine) scriptEngine);
		return "";
	}

	public void setUpdateFunction(String updateFunction) {
		this.updateFunction = new ScriptFunction(updateFunction);
	}

	public void setUpdateFunction(Consumer<UIObject> updateFunction) {
		this.updateFunction = updateFunction;
	}

	public void removeUpdateFunction() {
		this.updateFunction = NULL_FUNCTION;
	}

	public Consumer<UIObject> getUpdateFunction() {
		return updateFunction;
	}

	private static RenderTarget createRenderTarget() {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTexture(Texture.RGBA, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.asInt(),
				Options.RESOLUTION_Y.asInt());
		renderTarget.setRenderBuffer(RenderTarget.DEPTH24_STENCIL8, RenderTarget.DEPTH_STENCIL_ATTACHMENT,
				Options.RESOLUTION_X.asInt(), Options.RESOLUTION_Y.asInt());
		renderTarget.checkStatus();
		RenderTarget.unbind();
		return renderTarget;
	}

	public static class DragTask extends Task {

		private UIWindow window;
		private float dragX, dragY;

		public DragTask(UIWindow window) {
			this.window = window;
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindowSize();
			dragX = cursorPos.x / windowSize.x;
			dragY = 1 - cursorPos.y / windowSize.y;
		}

		@Override
		public void run() {
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindowSize();
			float mouseX = cursorPos.x / windowSize.x;
			float mouseY = 1 - cursorPos.y / windowSize.y;

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
