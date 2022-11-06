package org.jgine.system.systems.ui;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.SystemObject;

public abstract class UIObject implements SystemObject, Cloneable {

	UIWindow window;
	private float x;
	private float y;
	private float width;
	private float height;
	private Matrix transform;
	boolean isFocused;

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

	public abstract void onFocus();

	public abstract void onDefocus();

	public abstract void onClick(float mouseX, float mouseY);

	public abstract void onRelease(float mouseX, float mouseY);

	public abstract UIObjectType<?> getType();

	public void load(Map<String, Object> data) {
		x = YamlHelper.toFloat(data.get("x"));
		y = YamlHelper.toFloat(data.get("y"));
		width = YamlHelper.toFloat(data.get("width"));
		height = YamlHelper.toFloat(data.get("height"));
		float scale = YamlHelper.toFloat(data.get("scale"));
		if (scale != 0)
			setScale(scale);
		calculateTransform();
	}

	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		calculateTransform();
	}

	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(width);
		out.writeFloat(height);
		calculateTransform();
	}

	public final Matrix getTransform() {
		return transform;
	}

	protected void calculateTransform() {
		Transform.calculateMatrix(transform, -1 + (x + width * 0.5f) * 2, -1 + (y + height * 0.5f) * 2, 0, width,
				height, 0);
		if (hasWindow())
			transform.mult(window.getTransform());
	}

	public final void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		calculateTransform();
	}

	public final void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		calculateTransform();
	}

	public final void setX(float x) {
		this.x = x;
		calculateTransform();
	}

	public final float getX() {
		return x;
	}

	public final void setY(float y) {
		this.y = y;
		calculateTransform();
	}

	public final float getY() {
		return y;
	}

	public final void setScale(float scale) {
		this.width = scale;
		this.height = scale;
		calculateTransform();
	}

	public final void setScale(float width, float height) {
		this.width = width;
		this.height = height;
		calculateTransform();
	}

	public final void setWidth(float width) {
		this.width = width;
		calculateTransform();
	}

	public final float getWidth() {
		return width;
	}

	public final void setHeight(float height) {
		this.height = height;
		calculateTransform();
	}

	public final float getHeight() {
		return height;
	}

	public final void setWindow(@Nullable UIWindow window) {
		if (this.window != null)
			this.window.childs.remove(this);
		this.window = window;
		if (window != null)
			window.childs.add(this);
		calculateTransform();
	}

	@Nullable
	public final UIWindow getWindow() {
		return window;
	}

	public final boolean hasWindow() {
		return window != null;
	}

	public final boolean isFocused() {
		return isFocused;
	}
}
