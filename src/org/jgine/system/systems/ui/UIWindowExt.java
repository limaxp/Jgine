package org.jgine.system.systems.ui;

import org.jgine.misc.utils.options.Options;
import org.jgine.render.RenderTarget;
import org.jgine.render.Renderer;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;

public class UIWindowExt extends UIWindow {

	private RenderTarget renderTarget = createRenderTarget();

	public static RenderTarget createRenderTarget() {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTexture(Texture.RGBA, RenderTarget.COLOR_ATTACHMENT0, Options.RESOLUTION_X.getInt(),
				Options.RESOLUTION_Y.getInt());
		renderTarget.checkStatus();
		renderTarget.unbind();
		return renderTarget;
	}

	@Override
	protected void renderChilds() {
		RenderTarget tmp = Renderer.getRenderTarget();
		Renderer.setRenderTarget(renderTarget);
		renderTarget.clear();
		for (UIObject child : getVisibleChilds())
			child.render();
		Renderer.setRenderTarget(tmp);
		Material material = new Material(renderTarget.getTexture(RenderTarget.COLOR_ATTACHMENT0));
		material.flipY();
		UIRenderer.renderQuad(getTransform(), material);
	}

	@Override
	public UIObjectType<? extends UIWindowExt> getType() {
		return UIObjectTypes.WINDOW_EXT;
	}
}
