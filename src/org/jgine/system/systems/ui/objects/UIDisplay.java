package org.jgine.system.systems.ui.objects;

import org.jgine.core.entity.Entity;
import org.jgine.render.RenderTarget;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UIDisplay extends UIObject {

	private RenderTarget renderTarget;

	public UIDisplay() {
		this(1.0f);
	}

	public UIDisplay(float scale) {
		this(scale, scale);
	}

	public UIDisplay(float width, float height) {
//		renderTarget = new RenderTarget(width, height);
	}

	@Override
	protected void create(Entity entity) {
	}

	@Override
	protected void free() {
		renderTarget.close();
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), new Material(renderTarget.getTexture()));
	}

	@Override
	public void onFocus() {
	}

	@Override
	public void onDefocus() {
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
	}

	@Override
	public void onRelease(float mouseX, float mouseY) {
	}

	@Override
	public UIObjectType<? extends UIDisplay> getType() {
		return UIObjectTypes.DISPLAY;
	}
}
