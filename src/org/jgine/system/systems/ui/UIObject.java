package org.jgine.system.systems.ui;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public abstract class UIObject implements SystemObject, Cloneable {

	UIWindow window;
	private float x;
	private float y;
	private float width;
	private float height;
	private Matrix transform;
	boolean transformChanged;
	boolean isFocused;

	public UIObject() {
		transform = new Matrix();
		transformChanged = true;
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
			obj.transform = new Matrix();
			obj.transformChanged = true;
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected abstract void create(Entity entity);

	protected abstract void free();

	public abstract void render();

	public abstract void onFocus();

	public abstract void onDefocus();

	public abstract void onClick(float mouseX, float mouseY);

	public abstract void onRelease(float mouseX, float mouseY);

	public void load(Map<String, Object> data) {
		Object x = data.get("x");
		if (x != null && x instanceof Number)
			this.x = ((Number) x).floatValue();
		Object y = data.get("y");
		if (y != null && y instanceof Number)
			this.y = ((Number) y).floatValue();
		Object width = data.get("width");
		if (width != null && width instanceof Number)
			this.width = ((Number) width).floatValue();
		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.height = ((Number) height).floatValue();
		Object scale = data.get("scale");
		if (scale != null && scale instanceof Number)
			setScale(((Number) scale).floatValue());
	}

	public abstract UIObjectType<?> getType();

	public final Matrix getTransform() {
		if (!transformChanged)
			return transform;
		calculateTransform();
		transformChanged = false;
		return transform;
	}

	protected Matrix calculateTransform() {
		Transform.calculateMatrix(transform, new Vector3f(-1 + (x + width * 0.5f) * 2, -1 + (y + height * 0.5f) * 2, 0),
				new Vector3f(width, height, 0));
		if (hasWindow())
			transform.mult(window.getTransform());
		return transform;
	}

	public final void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		transformChanged = true;
	}

	public final void setX(float x) {
		this.x = x;
		transformChanged = true;
	}

	public final float getX() {
		return x;
	}

	public final void setY(float y) {
		this.y = y;
		transformChanged = true;
	}

	public final float getY() {
		return y;
	}

	public final void setScale(float scale) {
		this.width = scale;
		this.height = scale;
		transformChanged = true;
	}

	public final void setScale(float width, float height) {
		this.width = width;
		this.height = height;
		transformChanged = true;
	}

	public final void setWidth(float width) {
		this.width = width;
		transformChanged = true;
	}

	public final float getWidth() {
		return width;
	}

	public final void setHeight(float height) {
		this.height = height;
		transformChanged = true;
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
