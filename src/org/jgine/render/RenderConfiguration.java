package org.jgine.render;

import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector4f;

public class RenderConfiguration {

	private RenderTarget renderTarget;
	private float x;
	private float y;
	private float width;
	private float height;
	private Matrix matrix;

	public RenderConfiguration() {
		this(RenderTarget.createDefault(), 0, 0, 1, 1);
	}

	public RenderConfiguration(float x, float y, float width, float height) {
		this(RenderTarget.createDefault(), x, y, width, height);
	}

	public RenderConfiguration(RenderTarget renderTarget, float x, float y, float width, float height) {
		this.renderTarget = renderTarget;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		matrix = new Matrix();
		updateMatrix();
	}

	public RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public void setRenderTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}

	public void setX(float x) {
		this.x = x;
		updateMatrix();
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
		updateMatrix();
	}

	public float getY() {
		return y;
	}

	public void setPosition(Vector2f pos) {
		setPosition(pos.x, pos.y);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateMatrix();
	}

	public void setWidth(float width) {
		this.width = width;
		updateMatrix();
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
		updateMatrix();
	}

	public float getHeight() {
		return height;
	}

	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}

	public void setScale(float width, float height) {
		this.width = width;
		this.height = height;
		updateMatrix();
	}

	public void set(Vector4f values) {
		set(values.x, values.y, values.z, values.w);
	}

	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		updateMatrix();
	}

	public Matrix getMatrix() {
		return matrix;
	}

	private void updateMatrix() {
		Transform.calculateMatrix(matrix, -1 + (x + width * 0.5f) * 2, -1 + (y + height * 0.5f) * 2, 0, width, height,
				0);
	}
}
