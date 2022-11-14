package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.script.ScriptManager;
import org.jgine.system.SystemObject;

public abstract class UIObject implements SystemObject, Cloneable {

	UIWindow window;
	private float x;
	private float y;
	private float width;
	private float height;
	private Matrix transform;
	boolean isFocused;
	public String enableFunction;
	public String disableFunction;
	public String focusFunction;
	public String defocusFunction;
	public String clickFunction;
	public String releaseFunction;

	public UIObject() {
		transform = new Matrix();
		isFocused = false;
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

	public abstract void render();

	public abstract UIObjectType<?> getType();

	public void onEnable() {
		if (enableFunction != null)
			ScriptManager.invoke(window.scriptEngine, enableFunction, this);
	}

	public void onDisable() {
		if (disableFunction != null)
			ScriptManager.invoke(window.scriptEngine, disableFunction, this);
	}

	public void onFocus() {
		if (focusFunction != null)
			ScriptManager.invoke(window.scriptEngine, focusFunction, this);
	}

	public void onDefocus() {
		if (defocusFunction != null)
			ScriptManager.invoke(window.scriptEngine, defocusFunction, this);
	}

	public void onClick(float mouseX, float mouseY) {
		if (clickFunction != null)
			ScriptManager.invoke(window.scriptEngine, clickFunction, this);
	}

	public void onRelease(float mouseX, float mouseY) {
		if (releaseFunction != null)
			ScriptManager.invoke(window.scriptEngine, releaseFunction, this);
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
			enableFunction = YamlHelper.toString(enableFunctionData);
		Object disableFunctionData = data.get("onDisable");
		if (disableFunctionData != null)
			disableFunction = YamlHelper.toString(disableFunctionData);
		Object focusFunctionData = data.get("onFocus");
		if (focusFunctionData != null)
			focusFunction = YamlHelper.toString(focusFunctionData);
		Object defocusFunctionData = data.get("onDefocus");
		if (defocusFunctionData != null)
			defocusFunction = YamlHelper.toString(defocusFunctionData);
		Object clickFunctionData = data.get("onClick");
		if (clickFunctionData != null)
			clickFunction = YamlHelper.toString(clickFunctionData);
		Object releaseFunctionData = data.get("onRelease");
		if (releaseFunctionData != null)
			releaseFunction = YamlHelper.toString(releaseFunctionData);
		calculateTransform();
	}

	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		enableFunction = in.readUTF();
		disableFunction = in.readUTF();
		focusFunction = in.readUTF();
		defocusFunction = in.readUTF();
		clickFunction = in.readUTF();
		releaseFunction = in.readUTF();
		calculateTransform();
	}

	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(width);
		out.writeFloat(height);
		out.writeUTF(enableFunction);
		out.writeUTF(disableFunction);
		out.writeUTF(focusFunction);
		out.writeUTF(defocusFunction);
		out.writeUTF(clickFunction);
		out.writeUTF(releaseFunction);
	}

	public Matrix getTransform() {
		return transform;
	}

	protected void calculateTransform() {
		Transform.calculateMatrix(transform, -1 + (x + width * 0.5f) * 2, -1 + (y + height * 0.5f) * 2, 0, width,
				height, 0);
		if (hasWindow())
			transform.mult(window.getTransform());
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
	public UIWindow getWindow() {
		return window;
	}

	public boolean hasWindow() {
		return window != null;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public String getEnableFunction() {
		return enableFunction;
	}

	public void setEnableFunction(String enableFunction) {
		this.enableFunction = enableFunction;
	}

	public String getDisableFunction() {
		return disableFunction;
	}

	public void setDisableFunction(String disableFunction) {
		this.disableFunction = disableFunction;
	}

	public String getFocusFunction() {
		return focusFunction;
	}

	public void setFocusFunction(String focusFunction) {
		this.focusFunction = focusFunction;
	}

	public String getDefocusFunction() {
		return defocusFunction;
	}

	public void setDefocusFunction(String defocusFunction) {
		this.defocusFunction = defocusFunction;
	}

	public String getClickFunction() {
		return clickFunction;
	}

	public void setClickFunction(String clickFunction) {
		this.clickFunction = clickFunction;
	}

	public String getReleaseFunction() {
		return releaseFunction;
	}

	public void setReleaseFunction(String releaseFunction) {
		this.releaseFunction = releaseFunction;
	}
}
