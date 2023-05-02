package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.system.SystemObject;
import org.jgine.utils.Color;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.script.ScriptManager;

public abstract class UIObject implements SystemObject, Cloneable {

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
	boolean isFocused;
	public Consumer<UIObject> enableFunction;
	public Consumer<UIObject> disableFunction;
	public Consumer<UIObject> focusFunction;
	public Consumer<UIObject> defocusFunction;
	public BiConsumer<UIObject, Integer> clickFunction;
	public BiConsumer<UIObject, Integer> releaseFunction;
	public BiConsumer<UIObject, Float> scrollFunction;

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

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
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

	protected abstract void free();

	public abstract void render(int depth);

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

	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object widthData = data.get("width");
		if (widthData != null)
			width = YamlHelper.toFloat(widthData);
		Object heightData = data.get("height");
		if (heightData != null)
			height = YamlHelper.toFloat(heightData);
		Object scaleData = data.get("scale");
		if (scaleData != null)
			setScale(YamlHelper.toFloat(scaleData));
		Object enableFunctionData = data.get("onEnable");
		if (enableFunctionData != null)
			enableFunction = new ScriptFunction(YamlHelper.toString(enableFunctionData));
		Object disableFunctionData = data.get("onDisable");
		if (disableFunctionData != null)
			disableFunction = new ScriptFunction(YamlHelper.toString(disableFunctionData));
		Object focusFunctionData = data.get("onFocus");
		if (focusFunctionData != null)
			focusFunction = new ScriptFunction(YamlHelper.toString(focusFunctionData));
		Object defocusFunctionData = data.get("onDefocus");
		if (defocusFunctionData != null)
			defocusFunction = new ScriptFunction(YamlHelper.toString(defocusFunctionData));
		Object clickFunctionData = data.get("onClick");
		if (clickFunctionData != null)
			clickFunction = new ScriptValueFunction<Integer>(YamlHelper.toString(clickFunctionData));
		Object releaseFunctionData = data.get("onRelease");
		if (releaseFunctionData != null)
			releaseFunction = new ScriptValueFunction<Integer>(YamlHelper.toString(releaseFunctionData));
		Object scrollFunctionData = data.get("onScroll");
		if (scrollFunctionData != null)
			scrollFunction = new ScriptValueFunction<Float>(YamlHelper.toString(scrollFunctionData));
		calculateTransform();
	}

	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		enableFunction = loadFunction(in);
		disableFunction = loadFunction(in);
		focusFunction = loadFunction(in);
		defocusFunction = loadFunction(in);
		clickFunction = loadValueFunction(in);
		releaseFunction = loadValueFunction(in);
		scrollFunction = loadValueFunction(in);
		calculateTransform();
	}

	private static Consumer<UIObject> loadFunction(DataInput in) throws IOException {
		String funcName = in.readUTF();
		if (funcName.isEmpty())
			return NULL_FUNCTION;
		return new ScriptFunction(funcName);
	}

	@SuppressWarnings("unchecked")
	private static <T> BiConsumer<UIObject, T> loadValueFunction(DataInput in) throws IOException {
		String funcName = in.readUTF();
		if (funcName.isEmpty())
			return NULL_VALUE_FUNCTION;
		return new ScriptValueFunction<T>(funcName);
	}

	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(width);
		out.writeFloat(height);
		saveFunction(out, enableFunction);
		saveFunction(out, disableFunction);
		saveFunction(out, focusFunction);
		saveFunction(out, defocusFunction);
		saveFunction(out, clickFunction);
		saveFunction(out, releaseFunction);
		saveFunction(out, scrollFunction);
	}

	private static void saveFunction(DataOutput out, Consumer<UIObject> func) throws IOException {
		if (func instanceof ScriptFunction)
			out.writeUTF(((ScriptFunction) func).functionName);
		else
			out.writeUTF("");
	}

	private static void saveFunction(DataOutput out, BiConsumer<UIObject, ?> func) throws IOException {
		if (func instanceof ScriptFunction)
			out.writeUTF(((ScriptFunction) func).functionName);
		else
			out.writeUTF("");
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

	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		calculateTransform();
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

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
		calculateTransform();
	}

	public float getY() {
		return y;
	}

	public void setScale(float scale) {
		this.width = scale;
		this.height = scale;
		calculateTransform();
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

	@Nullable
	public UICompound getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public UIWindow getWindow() {
		UICompound window = parent;
		while (!(window instanceof UIWindow)) {
			window = window.parent;
		}
		return (UIWindow) window;
	}

	public boolean isFocused() {
		return isFocused;
	}

	protected ScriptEngine getScriptEngine() {
		return parent.getScriptEngine();
	}

	public Consumer<UIObject> getEnableFunction() {
		return enableFunction;
	}

	public boolean hasEnableFunction() {
		return enableFunction != NULL_FUNCTION;
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

	public Consumer<UIObject> getDisableFunction() {
		return disableFunction;
	}

	public boolean hasDisableFunction() {
		return disableFunction != NULL_FUNCTION;
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

	public Consumer<UIObject> getFocusFunction() {
		return focusFunction;
	}

	public boolean hasFocusFunction() {
		return focusFunction != NULL_FUNCTION;
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

	public Consumer<UIObject> getDefocusFunction() {
		return defocusFunction;
	}

	public boolean hasDefocusFunction() {
		return defocusFunction != NULL_FUNCTION;
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

	public BiConsumer<UIObject, Integer> getClickFunction() {
		return clickFunction;
	}

	public boolean hasClickFunction() {
		return clickFunction != NULL_VALUE_FUNCTION;
	}

	public void setClickFunction(String clickFunction) {
		this.clickFunction = new ScriptValueFunction<Integer>(clickFunction);
	}

	public void setClickFunction(BiConsumer<UIObject, Integer> clickFunction) {
		this.clickFunction = clickFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeClickFunction() {
		this.clickFunction = NULL_VALUE_FUNCTION;
	}

	public BiConsumer<UIObject, Integer> getReleaseFunction() {
		return releaseFunction;
	}

	public boolean hasReleaseFunction() {
		return releaseFunction != NULL_VALUE_FUNCTION;
	}

	public void setReleaseFunction(String releaseFunction) {
		this.releaseFunction = new ScriptValueFunction<Integer>(releaseFunction);
	}

	public void setReleaseFunction(BiConsumer<UIObject, Integer> releaseFunction) {
		this.releaseFunction = releaseFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeReleaseFunction() {
		this.releaseFunction = NULL_VALUE_FUNCTION;
	}

	public BiConsumer<UIObject, Float> getScrollFunction() {
		return scrollFunction;
	}

	public boolean hasScrollFunction() {
		return scrollFunction != NULL_VALUE_FUNCTION;
	}

	public void setScrollFunction(String scrollFunction) {
		this.scrollFunction = new ScriptValueFunction<Float>(scrollFunction);
	}

	public void setScrollFunction(BiConsumer<UIObject, Float> scrollFunction) {
		this.scrollFunction = scrollFunction;
	}

	@SuppressWarnings("unchecked")
	public void removeScrollFunction() {
		this.scrollFunction = NULL_VALUE_FUNCTION;
	}

	public static class ScriptFunction implements Consumer<UIObject> {

		public String functionName;

		public ScriptFunction(String functionName) {
			this.functionName = functionName;
		}

		@Override
		public void accept(UIObject object) {
			ScriptManager.invoke(object.getScriptEngine(), functionName, object);
		}
	}

	public static class ScriptValueFunction<E> extends ScriptFunction implements BiConsumer<UIObject, E> {

		public ScriptValueFunction(String functionName) {
			super(functionName);
		}

		@Override
		public void accept(UIObject object, E value) {
			ScriptManager.invoke(object.getScriptEngine(), functionName, object, value);
		}
	}
}
