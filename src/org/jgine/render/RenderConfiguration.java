package org.jgine.render;

import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.RenderTarget.Attachment;
import org.jgine.render.graphic.material.Texture;

public class RenderConfiguration implements AutoCloseable {

	private RenderTarget renderTarget;
	private RenderTarget intermediateTarget;
	private float x;
	private float y;
	private float width;
	private float height;

	public RenderConfiguration() {
		this(RenderTarget.createDefaultRenderBufferMultisample(Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt(), Options.ANTI_ALIASING.getInt()), 0, 0, 1, 1);
	}

	public RenderConfiguration(float x, float y, float width, float height) {
		this(RenderTarget.createDefaultRenderBufferMultisample(Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt(), Options.ANTI_ALIASING.getInt()), x, y, width, height);
	}

	public RenderConfiguration(RenderTarget renderTarget, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		setRenderTarget(renderTarget);
	}

	@Override
	public void close() {
		renderTarget.close();
		renderTarget = null;
		intermediateTarget.close();
		intermediateTarget = null;
	}

	public RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public void setRenderTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
		if (this.intermediateTarget != null)
			this.intermediateTarget.close();
		Attachment attachment = renderTarget.getAttachment(RenderTarget.COLOR_ATTACHMENT0);
		this.intermediateTarget = new RenderTarget();
		this.intermediateTarget.bind();
		this.intermediateTarget.setRenderBuffer(Texture.RGB, RenderTarget.COLOR_ATTACHMENT0, attachment.getWidth(),
				attachment.getHeight());
		this.intermediateTarget.checkStatus();
		this.intermediateTarget.unbind();
	}

	public RenderTarget getIntermediateTarget() {
		return intermediateTarget;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
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
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
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
	}

	public void set(Vector4f values) {
		set(values.x, values.y, values.z, values.w);
	}

	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
