package org.jgine.render.graphic.material;

import org.jgine.render.RenderTarget.Attachment;

public interface ITexture extends AutoCloseable, Attachment {

	public String getName();

	public int getWidth();

	public int getHeight();

	public int getColums();

	public int getRows();

	public void bind();

	public void unbind();

	@Override
	public void close();

	public default TextureAnimationHandler createAnimationHandler() {
		return TextureAnimationHandler.NONE;
	}
}
