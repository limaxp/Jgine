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
		this(RASTER_SIZE);
	}

	public UIDisplay(int scale) {
		this(scale, scale);
	}

	public UIDisplay(int width, int height) {
		renderTarget = new RenderTarget(width, height);
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
	public void onClick(int mouseX, int mouseY) {
	}

	@Override
	public void onRelease(int mouseX, int mouseY) {
	}

	@Override
	public UIObjectType<? extends UIDisplay> getType() {
		return UIObjectTypes.DISPLAY;
	}
}
