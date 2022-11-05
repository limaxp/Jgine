package org.jgine.render.graphic.material;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexParameteriv;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_CLAMP;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.stb.STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.FastMath;
import org.jgine.render.graphic.material.TextureAnimationHandler.AnimationFrame;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class Texture implements ITexture, AutoCloseable {

	// Some filters, included here for convenience
	public static final int LINEAR = GL_LINEAR;
	public static final int NEAREST = GL_NEAREST;
	public static final int LINEAR_MIPMAP_LINEAR = GL_LINEAR_MIPMAP_LINEAR;
	public static final int LINEAR_MIPMAP_NEAREST = GL_LINEAR_MIPMAP_NEAREST;
	public static final int NEAREST_MIPMAP_NEAREST = GL_NEAREST_MIPMAP_NEAREST;
	public static final int NEAREST_MIPMAP_LINEAR = GL_NEAREST_MIPMAP_LINEAR;

	// Some wrap modes, included here for convenience
	public static final int CLAMP = GL_CLAMP;
	public static final int CLAMP_TO_EDGE = GL_CLAMP_TO_EDGE;
	public static final int REPEAT = GL_REPEAT;

	public static final int DEFAULT_FILTER = NEAREST;
	public static final int DEFAULT_WRAP = REPEAT;

	public static final Texture NONE;

	static {
		NONE = new Texture();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer buffer = stack.malloc(4);
			buffer.put((byte) 255);
			buffer.put((byte) 255);
			buffer.put((byte) 255);
			buffer.put((byte) 255);
			buffer.flip();
			NONE.load(buffer, 1, 1, GL11.GL_RGBA);
		}
		NONE.colums = 1;
		NONE.rows = 1;
	}

	public final String name;
	private int id;
	private int width;
	private int height;
	private int colums;
	private int rows;
	private AnimationFrame[] animation;

	public Texture() {
		this.name = "";
	}

	public Texture(String name) {
		this.name = name;
	}

	@Override
	public final void close() {
		glDeleteBuffers(id);
		id = 0;
	}

	public final void load(BufferedImage image) {
		load(image, 1, 1);
	}

	public final void load(BufferedImage image, int colums, int rows) {
		close();
		id = glGenTextures();
		this.colums = colums;
		this.rows = rows;
		width = image.getWidth();
		height = image.getHeight();
		bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buildBuffer(image));
		setDefaultParameter();
	}

	public final void load(Image image) {
		load(image, 1, 1);
	}

	public final void load(Image image, int colums, int rows) {
		close();
		id = glGenTextures();
		this.colums = colums;
		this.rows = rows;
		width = image.width;
		height = image.height;
		bind();
		loadImage(image);
		setDefaultParameter();
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int format) {
		load(data, width, height, 1, 1, format, format);
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int internalformat, int format) {
		load(data, width, height, 1, 1, internalformat, format);
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int colums, int rows, int format) {
		load(data, width, height, colums, rows, format, format);
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int colums, int rows, int internalformat,
			int format) {
		close();
		id = glGenTextures();
		this.colums = colums;
		this.rows = rows;
		this.width = width;
		this.height = height;
		bind();
		glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
		setDefaultParameter();
	}

	private final void setDefaultParameter() {
		setWrap(DEFAULT_WRAP);
		setFilter(DEFAULT_FILTER);
		// glGenerateMipmap(GL_TEXTURE_2D);
	}

	public void setFilter(int filter) {
		setFilter(filter, filter);
	}

	public void setFilter(int minFilter, int magFilter) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
	}

	public void setWrap(int wrap) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
	}

	public final void setParameteri(int parameter, int value) {
		glTexParameteri(GL_TEXTURE_2D, parameter, value);
	}

	public final void setParameterf(int parameter, int value) {
		glTexParameterf(GL_TEXTURE_2D, parameter, value);
	}

	public final void setParameteriv(int parameter, int... values) {
		glTexParameteriv(GL_TEXTURE_2D, parameter, values);
	}

	public final void setParameteriv(int parameter, IntBuffer values) {
		glTexParameteriv(GL_TEXTURE_2D, parameter, values);
	}

	@Override
	public final void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	@Override
	public final void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public final void update(int xOffset, int yOffset, ByteBuffer buffer) {
		glTexSubImage2D(GL_TEXTURE_2D, 0, xOffset, yOffset, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	}

	public final int getId() {
		return id;
	}

	public final void setGrid(int colums, int rows) {
		this.colums = colums;
		this.rows = rows;
	}

	public final void setColums(int colums) {
		this.colums = colums;
	}

	@Override
	public final int getColums() {
		return colums;
	}

	public final void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public final int getRows() {
		return rows;
	}

	@Override
	public final int getWidth() {
		return width;
	}

	@Override
	public final int getHeight() {
		return height;
	}

	@Override
	public String getName() {
		return name;
	}

	public final void setAnimation(AnimationFrame[] animation) {
		this.animation = animation;
	}

	public final AnimationFrame[] getAnimation() {
		return animation;
	}

	@Override
	public final TextureAnimationHandler createAnimationHandler() {
		if (animation == null)
			return TextureAnimationHandler.NONE;
		return new TextureAnimationHandler(animation);
	}

	private static ByteBuffer buildBuffer(BufferedImage image) {
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * 4);
		boolean hasAlpha = image.getColorModel().hasAlpha();

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) ((pixel) & 0xFF));
				if (hasAlpha)
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				else
					buffer.put((byte) (0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}

	private static void loadImage(Image image) {
		int format;
		if (image.components == 3) {
			if ((image.width & 3) != 0) {
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (image.width & 1));
			}
			format = GL_RGB;
		} else {
			premultiplyAlpha(image);
			format = GL_RGBA;
		}

		glTexImage2D(GL_TEXTURE_2D, 0, format, image.width, image.height, 0, format, GL_UNSIGNED_BYTE, image.data);

		ByteBuffer input_pixels = image.data;
		int input_w = image.width;
		int input_h = image.height;
		int mipmapLevel = 0;
		while (1 < input_w || 1 < input_h) {
			int output_w = Math.max(1, input_w >> 1);
			int output_h = Math.max(1, input_h >> 1);

			ByteBuffer output_pixels = memAlloc(output_w * output_h * image.components);
			stbir_resize_uint8_generic(input_pixels, input_w, input_h, input_w * image.components, output_pixels,
					output_w, output_h, output_w * image.components, image.components,
					image.components == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
					STBIR_EDGE_CLAMP, STBIR_FILTER_MITCHELL, STBIR_COLORSPACE_SRGB);

			if (mipmapLevel == 0) {
				stbi_image_free(image.data);
			} else {
				memFree(input_pixels);
			}

			glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE,
					output_pixels);

			input_pixels = output_pixels;
			input_w = output_w;
			input_h = output_h;
		}
		if (mipmapLevel == 0) {
			stbi_image_free(image.data);
		} else {
			memFree(input_pixels);
		}
	}

	private static void premultiplyAlpha(Image image) {
		ByteBuffer data = image.data;
		int stride = image.width * 4;
		for (int y = 0; y < image.height; y++) {
			for (int x = 0; x < image.width; x++) {
				int i = y * stride + x * 4;

				float alpha = (data.get(i + 3) & 0xFF) / 255.0f;
				data.put(i + 0, (byte) FastMath.round(((data.get(i + 0) & 0xFF) * alpha)));
				data.put(i + 1, (byte) FastMath.round(((data.get(i + 1) & 0xFF) * alpha)));
				data.put(i + 2, (byte) FastMath.round(((data.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}
}
