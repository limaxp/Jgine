package org.jgine.render;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import org.jgine.core.Engine;
import org.jgine.core.window.Window;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.graphic.material.ITexture;
import org.jgine.render.graphic.material.Texture;

public class RenderTarget implements ITexture, AutoCloseable {

	public static final RenderTarget NONE = new RenderTarget() {

		@Override
		public void begin() {
			super.end();
		}

		@Override
		public void end() {
		}
	};

	protected int fbo;
	protected Texture texture;
	protected int depthBuffer;

	protected RenderTarget() {
	}

	public RenderTarget(int width, int height) {
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		texture = new Texture();
		texture.load(null, width, height, GL_RGB);

		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);

		glDrawBuffers(GL_COLOR_ATTACHMENT0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
			Logger.warn("RenderTarget: Error creating FrameBuffer!");

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	@Override
	public void close() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(fbo);
		texture.close();
		fbo = 0;
	}

	public void begin() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glViewport(0, 0, texture.getWidth(), texture.getHeight());
	}

	public void end() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		Window window = Engine.getInstance().getWindow();
		glViewport(0, 0, window.getResolutionX(), window.getResolutionY());
	}

	public final Texture getTexture() {
		return texture;
	}

	@Override
	public int getWidth() {
		return texture.getWidth();
	}

	@Override
	public int getHeight() {
		return texture.getHeight();
	}

	@Override
	public int getColums() {
		return texture.getColums();
	}

	@Override
	public int getRows() {
		return -texture.getRows(); // minus to flip upside down!
	}

	@Override
	public void bind() {
		texture.bind();
	}

	@Override
	public void unbind() {
		texture.unbind();
	}
}
