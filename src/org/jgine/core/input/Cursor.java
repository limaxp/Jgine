package org.jgine.core.input;

import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwDestroyCursor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.jgine.render.graphic.material.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

public class Cursor {

	private static final Map<String, Cursor> CURSORS = new HashMap<String, Cursor>();

	public static void terminate() {
		for (Cursor cursor : CURSORS.values())
			cursor.delete();
	}

	public static Cursor create(String name, BufferedImage image, int xhot, int yhot) {
		return create(name, imageToGLFWImage(image), xhot, yhot);
	}

	public static Cursor create(String name, Image image, int xhot, int yhot) {
		return create(name, imageToGLFWImage(image), xhot, yhot);
	}

	public static Cursor create(String name, GLFWImage image, int xhot, int yhot) {
		Cursor cursor = new Cursor(glfwCreateCursor(image, xhot, yhot), name, image, xhot, yhot);
		CURSORS.put(name, cursor);
		return cursor;
	}

	public static Cursor createStandardCursor(String name, int shape) {
		Cursor cursor = new Cursor(glfwCreateStandardCursor(shape), name);
		CURSORS.put(name, cursor);
		return cursor;
	}

	public final long id;
	public final String name;
	public final GLFWImage image;
	public final int xhot;
	public final int yhot;

	public Cursor(long id, String name) {
		this(id, name, null, 0, 0);
	}

	public Cursor(long id, String name, GLFWImage image, int xhot, int yhot) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.xhot = xhot;
		this.yhot = yhot;
	}

	public void delete() {
		glfwDestroyCursor(id);
	}

	public static GLFWImage imageToGLFWImage(BufferedImage image) {
		if (image.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
			final BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics2D graphics = convertedImage.createGraphics();
			final int targetWidth = image.getWidth();
			final int targetHeight = image.getHeight();
			graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
			graphics.dispose();
			image = convertedImage;
		}
		final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				buffer.put((byte) ((colorSpace << 8) >> 24));
				buffer.put((byte) ((colorSpace << 16) >> 24));
				buffer.put((byte) ((colorSpace << 24) >> 24));
				buffer.put((byte) (colorSpace >> 24));
			}
		}
		buffer.flip();
		final GLFWImage result = GLFWImage.create();
		result.set(image.getWidth(), image.getHeight(), buffer);
		return result;
	}

	public static GLFWImage imageToGLFWImage(Image image) {
		final GLFWImage result = GLFWImage.create();
		result.set(image.width, image.height, image.data);
		return result;
	}
}
