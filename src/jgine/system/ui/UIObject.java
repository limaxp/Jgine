package jgine.system.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.system.SystemObject;
import jgine.system.script.ScriptBase;
import jgine.system.script.ScriptManager;
import jgine.utils.Color;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;

public abstract class UIObject implements SystemObject {

	public static final int BACKGROUND_COLOR = Color.rgb(0.15f, 0.15f, 0.15f);
	public static final int BORDER_COLOR = Color.rgb(0.3f, 0.3f, 0.3f);
	public static final int FOCUS_COLOR = Color.rgb(0.5f, 0.5f, 0.5f);

	public static final Consumer<UIObject> NULL_FUNCTION = new Consumer<UIObject>() {

		@Override
		public void accept(UIObject t) {
		}
	};

	@SuppressWarnings("rawtypes")
	public static final BiConsumer NULL_VALUE_FUNCTION = new BiConsumer() {

		@Override
		public void accept(Object t, Object u) {
		}
	};

	UICompound parent;
	private float x;
	private float y;
	private float width;
	private float height;
	private Matrix transform;
	public boolean isFocused;
	private Consumer<UIObject> enableFunction;
	private Consumer<UIObject> disableFunction;
	private Consumer<UIObject> focusFunction;
	private Consumer<UIObject> defocusFunction;
	private BiConsumer<UIObject, Integer> clickFunction;
	private BiConsumer<UIObject, Integer> releaseFunction;
	private BiConsumer<UIObject, Float> scrollFunction;

	@SuppressWarnings("unchecked")
	public UIObject() {
		transform = new Matrix();
		isFocused = false;
		enableFunction = NULL_FUNCTION;
		disableFunction = NULL_FUNCTION;
		focusFunction = NULL_FUNCTION;
		defocusFunction = NULL_FUNCTION;
		clickFunction = NULL_VALUE_FUNCTION;
		releaseFunction = NULL_VALUE_FUNCTION;
		scrollFunction = NULL_VALUE_FUNCTION;
	}

	protected abstract void free();

	public abstract void render();

	public abstract UIObjectType<?> getType();

	public void onEnable() {
		enableFunction.accept(this);
	}

	public void onDisable() {
		disableFunction.accept(this);
	}

	public void onFocus() {
		focusFunction.accept(this);
	}

	public void onDefocus() {
		defocusFunction.accept(this);
	}

	public void onClick(int key) {
		clickFunction.accept(this, key);
	}

	public void onRelease(int key) {
		releaseFunction.accept(this, key);
	}

	public void onScroll(float scroll) {
		scrollFunction.accept(this, scroll);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		width = ObjectUtils.toFloat(data.get("width"), width);
		height = ObjectUtils.toFloat(data.get("height"), height);
		Object scaleData = data.get("scale");
		if (scaleData != null) {
			float scale = ObjectUtils.toFloat(scaleData);
			setScale(scale, scale);
		}

		Object enableFunctionData = data.get("onEnable");
		if (enableFunctionData != null)
			enableFunction = new ScriptFunction(ObjectUtils.toString(enableFunctionData));
		Object disableFunctionData = data.get("onDisable");
		if (disableFunctionData != null)
			disableFunction = new ScriptFunction(ObjectUtils.toString(disableFunctionData));
		Object focusFunctionData = data.get("onFocus");
		if (focusFunctionData != null)
			focusFunction = new ScriptFunction(ObjectUtils.toString(focusFunctionData));
		Object defocusFunctionData = data.get("onDefocus");
		if (defocusFunctionData != null)
			defocusFunction = new ScriptFunction(ObjectUtils.toString(defocusFunctionData));
		Object clickFunctionData = data.get("onClick");
		if (clickFunctionData != null)
			clickFunction = new ScriptValueFunction<Integer>(ObjectUtils.toString(clickFunctionData), int.class);
		Object releaseFunctionData = data.get("onRelease");
		if (releaseFunctionData != null)
			releaseFunction = new ScriptValueFunction<Integer>(ObjectUtils.toString(releaseFunctionData), int.class);
		Object scrollFunctionData = data.get("onScroll");
		if (scrollFunctionData != null)
			scrollFunction = new ScriptValueFunction<Float>(ObjectUtils.toString(scrollFunctionData), float.class);
		calculateTransform();
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("width", width);
		data.put("height", height);
		data.put("onEnable", getFunctionName(enableFunction));
		data.put("onDisable", getFunctionName(disableFunction));
		data.put("onFocus", getFunctionName(focusFunction));
		data.put("onDefocus", getFunctionName(defocusFunction));
		data.put("onClick", getFunctionName(clickFunction));
		data.put("onRelease", getFunctionName(releaseFunction));
		data.put("onScroll", getFunctionName(scrollFunction));
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		enableFunction = loadFunction(in);
		disableFunction = loadFunction(in);
		focusFunction = loadFunction(in);
		defocusFunction = loadFunction(in);
		clickFunction = loadValueFunction(in, int.class);
		releaseFunction = loadValueFunction(in, int.class);
		scrollFunction = loadValueFunction(in, float.class);
		calculateTransform();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(width);
		out.writeFloat(height);
		out.writeUTF(getFunctionName(enableFunction));
		out.writeUTF(getFunctionName(disableFunction));
		out.writeUTF(getFunctionName(focusFunction));
		out.writeUTF(getFunctionName(defocusFunction));
		out.writeUTF(getFunctionName(clickFunction));
		out.writeUTF(getFunctionName(releaseFunction));
		out.writeUTF(getFunctionName(scrollFunction));

	}

	protected static Consumer<UIObject> loadFunction(DataInput in) throws IOException {
		String funcName = in.readUTF();
		if (funcName.isEmpty())
			return NULL_FUNCTION;
		return new ScriptFunction(funcName);
	}

	@SuppressWarnings("unchecked")
	protected static <T> BiConsumer<UIObject, T> loadValueFunction(DataInput in, Class<?> clazz) throws IOException {
		String funcName = in.readUTF();
		if (funcName.isEmpty())
			return NULL_VALUE_FUNCTION;
		return new ScriptValueFunction<T>(funcName, clazz);
	}

	@Override
	public int system() {
		return Engine.UI;
	}

	@Override
	public UIObject clone() {
		try {
			UIObject obj = (UIObject) super.clone();
			obj.transform = new Matrix(transform);
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static String getFunctionName(Object func) {
		if (func instanceof ScriptFunction)
			return ((ScriptFunction) func).functionName;
		else
			return "";
	}

	public Matrix getTransform() {
		return transform;
	}

	protected void calculateTransform() {
		transform.clear();
		transform.setPosition(-1.0f + (x + width * 0.5f) * 2.0f, -1.0f + (y + height * 0.5f) * 2.0f, 0.0f);
		transform.scaling(width, height, 0.0f);
		if (hasParent())
			parent.updateTransform(transform);
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		calculateTransform();
	}

	public void setX(float x) {
		this.x = x;
		calculateTransform();
	}

	public void setY(float y) {
		this.y = y;
		calculateTransform();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setScale(float width, float height) {
		this.width = width;
		this.height = height;
		calculateTransform();
	}

	public void setWidth(float width) {
		this.width = width;
		calculateTransform();
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
		calculateTransform();
	}

	public float getHeight() {
		return height;
	}
	
	public Entity getEntity() {
		return parent.entity;
	}

	@Nullable
	public UICompound getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public UIWindow getWindow() {
		return parent.getWindow();
	}

	public UIWindow getTopWindow() {
		UICompound parent = this.parent;
		if (parent == null)
			return (UIWindow) this;
		while (parent.parent != null)
			parent = parent.parent;
		return (UIWindow) parent;
	}

	public void forParents(Consumer<UICompound> func) {
		UICompound parent = this.parent;
		if (parent == null)
			return;
		do {
			func.accept(parent);
		} while ((parent = parent.parent) != null);
	}

	public boolean isFocused() {
		return isFocused;
	}

	protected Object getScriptEngine() {
		return getParent().getScriptEngine();
	}

	public void setEnableFunction(String enableFunction) {
		this.enableFunction = new ScriptFunction(enableFunction);
	}

	public void setEnableFunction(Consumer<UIObject> enableFunction) {
		this.enableFunction = enableFunction;
	}

	public void removeEnableFunction() {
		this.enableFunction = NULL_FUNCTION;
	}

	public Consumer<UIObject> getEnableFunction() {
		return enableFunction;
	}

	public void setDisableFunction(String disableFunction) {
		this.disableFunction = new ScriptFunction(disableFunction);
	}

	public void setDisableFunction(Consumer<UIObject> disableFunction) {
		this.disableFunction = disableFunction;
	}

	public void removeDisableFunction() {
		this.disableFunction = NULL_FUNCTION;
	}

	public Consumer<UIObject> getDisableFunction() {
		return disableFunction;
	}

	public void setFocusFunction(String focusFunction) {
		this.focusFunction = new ScriptFunction(focusFunction);
	}

	public void setFocusFunction(Consumer<UIObject> focusFunction) {
		this.focusFunction = focusFunction;
	}

	public void removeFocusFunction() {
		this.focusFunction = NULL_FUNCTION;
	}

	public Consumer<UIObject> getFocusFunction() {
		return focusFunction;
	}

	public void setDefocusFunction(String defocusFunction) {
		this.defocusFunction = new ScriptFunction(defocusFunction);
	}

	public void setDefocusFunction(Consumer<UIObject> defocusFunction) {
		this.defocusFunction = defocusFunction;
	}

	public void removeDefocusFunction() {
		this.defocusFunction = NULL_FUNCTION;
	}

	public Consumer<UIObject> getDefocusFunction() {
		return defocusFunction;
	}

	public void setClickFunction(String clickFunction) {
		this.clickFunction = new ScriptValueFunction<Integer>(clickFunction, int.class);
	}

	public void setClickFunction(BiConsumer<UIObject, Integer> clickFunction) {
		this.clickFunction = clickFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeClickFunction() {
		this.clickFunction = NULL_VALUE_FUNCTION;
	}

	public BiConsumer<UIObject, Integer> getClickFunction() {
		return clickFunction;
	}

	public void setReleaseFunction(String releaseFunction) {
		this.releaseFunction = new ScriptValueFunction<Integer>(releaseFunction, int.class);
	}

	public void setReleaseFunction(BiConsumer<UIObject, Integer> releaseFunction) {
		this.releaseFunction = releaseFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeReleaseFunction() {
		this.releaseFunction = NULL_VALUE_FUNCTION;
	}

	public BiConsumer<UIObject, Integer> getReleaseFunction() {
		return releaseFunction;
	}

	public void setScrollFunction(String scrollFunction) {
		this.scrollFunction = new ScriptValueFunction<Float>(scrollFunction, float.class);
	}

	public void setScrollFunction(BiConsumer<UIObject, Float> scrollFunction) {
		this.scrollFunction = scrollFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeScrollFunction() {
		this.scrollFunction = NULL_VALUE_FUNCTION;
	}

	public BiConsumer<UIObject, Float> getScrollFunction() {
		return scrollFunction;
	}

	public static class ScriptFunction implements Consumer<UIObject> {

		protected String functionName;

		public ScriptFunction(String functionName) {
			this.functionName = functionName;
		}

		@Override
		public void accept(UIObject object) {
			Object scriptEngine = object.getScriptEngine();
			if (scriptEngine instanceof ScriptBase)
				((ScriptBase) scriptEngine).invokeFunction(functionName, UIObject.class, object);
			else
				ScriptManager.invoke((ScriptEngine) scriptEngine, functionName, object);
		}
	}

	public static class ScriptValueFunction<E> extends ScriptFunction implements BiConsumer<UIObject, E> {

		protected Class<?> clazz;

		public ScriptValueFunction(String functionName, Class<?> clazz) {
			super(functionName);
			this.clazz = clazz;
		}

		@Override
		public void accept(UIObject object, E value) {
			Object scriptEngine = object.getScriptEngine();
			if (scriptEngine instanceof ScriptBase)
				((ScriptBase) scriptEngine).invokeFunction(functionName, UIObject.class, clazz, object, value);
			else
				ScriptManager.invoke((ScriptEngine) scriptEngine, functionName, object, value);
		}
	}
}
