package org.jgine.render;

import org.jgine.render.RenderTarget.Attachment;
import org.jgine.render.material.Texture;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector4f;
import org.jgine.utils.options.Options;

public class RenderConfiguration implements AutoCloseable {

	public static RenderConfiguration create(float x, float y, float width, float height) {
		RenderTarget renderTarget;
		int intAliasing = Options.ANTI_ALIASING.getInt();
		if (intAliasing <= 0)
			renderTarget = RenderTarget.createDefaultRenderBuffer(Options.RESOLUTION_X.getInt(),
					Options.RESOLUTION_Y.getInt());
		else
			renderTarget = RenderTarget.createDefaultRenderBufferMultisample(Options.RESOLUTION_X.getInt(),
					Options.RESOLUTION_Y.getInt(), intAliasing);
		return new RenderConfiguration(renderTarget, x, y, width, height);
	}

	private RenderTarget renderTarget;
	private RenderTarget intermediateTarget;
	private float x;
	private float y;
	private float width;
	private float height;

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
		RenderTarget.unbind();
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
