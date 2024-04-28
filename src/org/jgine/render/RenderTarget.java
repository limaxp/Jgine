package org.jgine.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT10;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT11;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT12;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT13;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT14;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT15;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT16;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT17;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT18;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT19;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT20;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT21;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT22;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT23;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT25;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT26;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT27;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT28;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT29;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT30;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT31;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT5;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT6;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT7;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT9;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_MAX_COLOR_ATTACHMENTS;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX8;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL30.glRenderbufferStorageMultisample;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Engine;
import org.jgine.render.material.Texture;
import org.jgine.utils.math.vector.Vector2i;

import maxLibs.utils.logger.Logger;

public class RenderTarget implements AutoCloseable {

	public static final int MAX_COLOR_ATTACHMENTS = GL_MAX_COLOR_ATTACHMENTS;

	// Some filters, included here for convenience
	public static final int COLOR_BUFFER_BIT = GL_COLOR_BUFFER_BIT;
	public static final int DEPTH_BUFFER_BIT = GL_DEPTH_BUFFER_BIT;
	public static final int STENCIL_BUFFER_BIT = GL_STENCIL_BUFFER_BIT;

	// Some attachments, included here for convenience
	public static final int COLOR_ATTACHMENT0 = GL_COLOR_ATTACHMENT0;
	public static final int COLOR_ATTACHMENT1 = GL_COLOR_ATTACHMENT1;
	public static final int COLOR_ATTACHMENT2 = GL_COLOR_ATTACHMENT2;
	public static final int COLOR_ATTACHMENT3 = GL_COLOR_ATTACHMENT3;
	public static final int COLOR_ATTACHMENT4 = GL_COLOR_ATTACHMENT4;
	public static final int COLOR_ATTACHMENT5 = GL_COLOR_ATTACHMENT5;
	public static final int COLOR_ATTACHMENT6 = GL_COLOR_ATTACHMENT6;
	public static final int COLOR_ATTACHMENT7 = GL_COLOR_ATTACHMENT7;
	public static final int COLOR_ATTACHMENT8 = GL_COLOR_ATTACHMENT8;
	public static final int COLOR_ATTACHMENT9 = GL_COLOR_ATTACHMENT9;
	public static final int COLOR_ATTACHMENT10 = GL_COLOR_ATTACHMENT10;
	public static final int COLOR_ATTACHMENT11 = GL_COLOR_ATTACHMENT11;
	public static final int COLOR_ATTACHMENT12 = GL_COLOR_ATTACHMENT12;
	public static final int COLOR_ATTACHMENT13 = GL_COLOR_ATTACHMENT13;
	public static final int COLOR_ATTACHMENT14 = GL_COLOR_ATTACHMENT14;
	public static final int COLOR_ATTACHMENT15 = GL_COLOR_ATTACHMENT15;
	public static final int COLOR_ATTACHMENT16 = GL_COLOR_ATTACHMENT16;
	public static final int COLOR_ATTACHMENT17 = GL_COLOR_ATTACHMENT17;
	public static final int COLOR_ATTACHMENT18 = GL_COLOR_ATTACHMENT18;
	public static final int COLOR_ATTACHMENT19 = GL_COLOR_ATTACHMENT19;
	public static final int COLOR_ATTACHMENT20 = GL_COLOR_ATTACHMENT20;
	public static final int COLOR_ATTACHMENT21 = GL_COLOR_ATTACHMENT21;
	public static final int COLOR_ATTACHMENT22 = GL_COLOR_ATTACHMENT22;
	public static final int COLOR_ATTACHMENT23 = GL_COLOR_ATTACHMENT23;
	public static final int COLOR_ATTACHMENT24 = GL_COLOR_ATTACHMENT24;
	public static final int COLOR_ATTACHMENT25 = GL_COLOR_ATTACHMENT25;
	public static final int COLOR_ATTACHMENT26 = GL_COLOR_ATTACHMENT26;
	public static final int COLOR_ATTACHMENT27 = GL_COLOR_ATTACHMENT27;
	public static final int COLOR_ATTACHMENT28 = GL_COLOR_ATTACHMENT28;
	public static final int COLOR_ATTACHMENT29 = GL_COLOR_ATTACHMENT29;
	public static final int COLOR_ATTACHMENT30 = GL_COLOR_ATTACHMENT30;
	public static final int COLOR_ATTACHMENT31 = GL_COLOR_ATTACHMENT31;
	public static final int DEPTH_ATTACHMENT = GL_DEPTH_ATTACHMENT;
	public static final int STENCIL_ATTACHMENT = GL_STENCIL_ATTACHMENT;
	public static final int DEPTH_STENCIL_ATTACHMENT = GL_DEPTH_STENCIL_ATTACHMENT;

	// Some internal formats, included here for convenience
	public static final int DEPTH_COMPONENT16 = GL_DEPTH_COMPONENT16;
	public static final int DEPTH_COMPONENT24 = GL_DEPTH_COMPONENT24;
	public static final int DEPTH_COMPONENT32F = GL_DEPTH_COMPONENT32F;
	public static final int DEPTH24_STENCIL8 = GL_DEPTH24_STENCIL8;
	public static final int DEPTH32F_STENCIL8 = GL_DEPTH32F_STENCIL8;
	public static final int STENCIL_INDEX8 = GL_STENCIL_INDEX8;

	public static RenderTarget createDefault(int width, int height) {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTexture(Texture.RGB, COLOR_ATTACHMENT0, width, height);
		renderTarget.setRenderBuffer(DEPTH24_STENCIL8, DEPTH_STENCIL_ATTACHMENT, width, height);
		renderTarget.checkStatus();
		unbind();
		return renderTarget;
	}

	public static RenderTarget createDefaultMultisample(int width, int height, int samples) {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setTextureMultisample(Texture.RGB, COLOR_ATTACHMENT0, samples, width, height);
		renderTarget.setRenderBufferMultisample(DEPTH24_STENCIL8, DEPTH_STENCIL_ATTACHMENT, samples, width, height);
		renderTarget.checkStatus();
		unbind();
		return renderTarget;
	}

	public static RenderTarget createDefaultRenderBuffer(int width, int height) {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setRenderBuffer(Texture.RGB, COLOR_ATTACHMENT0, width, height);
		renderTarget.setRenderBuffer(DEPTH24_STENCIL8, DEPTH_STENCIL_ATTACHMENT, width, height);
		renderTarget.checkStatus();
		unbind();
		return renderTarget;
	}

	public static RenderTarget createDefaultRenderBufferMultisample(int width, int height, int samples) {
		RenderTarget renderTarget = new RenderTarget();
		renderTarget.bind();
		renderTarget.setRenderBufferMultisample(Texture.RGB, COLOR_ATTACHMENT0, samples, width, height);
		renderTarget.setRenderBufferMultisample(DEPTH24_STENCIL8, DEPTH_STENCIL_ATTACHMENT, samples, width, height);
		renderTarget.checkStatus();
		unbind();
		return renderTarget;
	}

	protected int fbo;
	protected Attachment[] attachments;

	public RenderTarget() {
		fbo = glGenFramebuffers();
		attachments = new Attachment[MAX_COLOR_ATTACHMENTS + 2];
	}

	@Override
	public void close() {
		for (int i = 0; i < attachments.length; i++) {
			if (attachments[i] == null)
				continue;
			attachments[i].close();
			attachments[i] = null;
		}
		glDeleteFramebuffers(fbo);
		fbo = 0;
	}

	public boolean isClosed() {
		return fbo == 0;
	}

	public void checkStatus() {
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
			Logger.warn("RenderTarget: Error creating FrameBuffer!");
	}

	public int getAttachmentIndex(int attachment) {
		if (attachment == DEPTH_ATTACHMENT)
			return MAX_COLOR_ATTACHMENTS;
		else if (attachment == STENCIL_ATTACHMENT || attachment == GL_DEPTH_STENCIL_ATTACHMENT)
			return MAX_COLOR_ATTACHMENTS + 1;
		return attachment - GL_COLOR_ATTACHMENT0;
	}

	public Texture setTexture(int format, int attachment, int width, int height) {
		return setTexture(format, format, attachment, width, height);
	}

	public Texture setTexture(int format, int internalFormat, int attachment, int width, int height) {
		Texture texture = new Texture();
		attachments[getAttachmentIndex(attachment)] = texture;
		texture.load(null, width, height, format, internalFormat);
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, texture.getTarget(), texture.getId(), 0);
		return texture;
	}

	public Texture setTextureMultisample(int internalFormat, int attachment, int samples, int width, int height) {
		Texture texture = new Texture();
		attachments[getAttachmentIndex(attachment)] = texture;
		texture.loadMultisample(samples, width, height, internalFormat);
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, texture.getTarget(), texture.getId(), 0);
		return texture;
	}

	public RenderBuffer setRenderBuffer(int internalFormat, int attachment, int width, int height) {
		RenderBuffer buffer = new RenderBuffer(width, height);
		attachments[getAttachmentIndex(attachment)] = buffer;
		buffer.bind();
		glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height);
		buffer.unbind();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, buffer.rbo);
		return buffer;
	}

	public RenderBuffer setRenderBufferMultisample(int internalFormat, int attachment, int samples, int width,
			int height) {
		RenderBuffer buffer = new RenderBuffer(width, height);
		attachments[getAttachmentIndex(attachment)] = buffer;
		buffer.bind();
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, internalFormat, width, height);
		buffer.unbind();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, buffer.rbo);
		return buffer;
	}

	public void clear() {
		glClear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT);
	}

	public void bindViewport(int attachment) {
		bind();
		Attachment attach = getAttachment(attachment);
		glViewport(0, 0, attach.getWidth(), attach.getHeight());
	}

	public static void unbindViewport() {
		unbind();
		Vector2i windowSize = Engine.getInstance().getWindow().getSize();
		glViewport(0, 0, windowSize.x, windowSize.y);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}

	public static void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void bindRead() {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
	}

	public static void unbindRead() {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
	}

	public void bindDraw() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
	}

	public static void unbindDraw() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
	}

	public Texture getTexture(int attachment) {
		Attachment obj = attachments[getAttachmentIndex(attachment)];
		if (!(obj instanceof Texture))
			throw new IllegalArgumentException("attachment is not a texture!");
		return (Texture) obj;
	}

	public RenderBuffer getBuffer(int attachment) {
		Attachment obj = attachments[getAttachmentIndex(attachment)];
		if (!(obj instanceof RenderBuffer))
			throw new IllegalArgumentException("attachment is not a buffer!");
		return (RenderBuffer) obj;
	}

	@Nullable
	public Attachment getAttachment(int attachment) {
		return attachments[getAttachmentIndex(attachment)];
	}

	public static void blit(int srcX1, int srcY1, int srcX2, int srcY2, int dstX1, int dstY1, int dstX2, int dstY2,
			int mask, int filter) {
		glBlitFramebuffer(srcX1, srcY1, srcX2, srcY2, dstX1, dstY1, dstX2, dstY2, mask, filter);
	}

	public static interface Attachment extends AutoCloseable {

		@Override
		public void close();

		public int getWidth();

		public int getHeight();

		public void bind();

		public void unbind();
	}

	public static class RenderBuffer implements Attachment {

		public final int rbo;
		public final int width;
		public final int height;

		public RenderBuffer(int width, int height) {
			this.rbo = glGenRenderbuffers();
			this.width = width;
			this.height = height;
		}

		@Override
		public void close() {
			glDeleteRenderbuffers(rbo);
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public void bind() {
			glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		}

		@Override
		public void unbind() {
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
		}
	}
}
