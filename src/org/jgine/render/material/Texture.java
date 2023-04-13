package org.jgine.render.material;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_PROXY_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_INDEX;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexParameteriv;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_BYTE_2_3_3_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_BYTE_3_3_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_10_10_10_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_5_5_1;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_6_5;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_6_5_REV;
import static org.lwjgl.opengl.GL13.GL_PROXY_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.GL_BGRA_INTEGER;
import static org.lwjgl.opengl.GL30.GL_BGR_INTEGER;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_PROXY_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RGBA_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RGB_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RG_INTEGER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL31.GL_PROXY_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL32.GL_PROXY_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
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
import org.jgine.render.material.TextureAnimationHandler.TextureAnimation;
import org.jgine.utils.math.FastMath;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class Texture implements ITexture, AutoCloseable {

	// Some targets, included here for convenience
	public static final int TEXTURE_2D = GL_TEXTURE_2D;
	public static final int PROXY_TEXTURE_2D = GL_PROXY_TEXTURE_2D;
	public static final int TEXTURE_1D_ARRAY = GL_TEXTURE_1D_ARRAY;
	public static final int PROXY_TEXTURE_1D_ARRAY = GL_PROXY_TEXTURE_1D_ARRAY;
	public static final int TEXTURE_RECTANGLE = GL_TEXTURE_RECTANGLE;
	public static final int PROXY_TEXTURE_RECTANGLE = GL_PROXY_TEXTURE_RECTANGLE;
	public static final int TEXTURE_CUBE_MAP_POSITIVE_X = GL_TEXTURE_CUBE_MAP_POSITIVE_X;
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_X = GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
	public static final int TEXTURE_CUBE_MAP_POSITIVE_Y = GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_Y = GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
	public static final int TEXTURE_CUBE_MAP_POSITIVE_Z = GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
	public static final int TEXTURE_CUBE_MAP_NEGATIVE_Z = GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
	public static final int PROXY_TEXTURE_CUBE_MAP = GL_PROXY_TEXTURE_CUBE_MAP;

	// Some multisample targets, included here for convenience
	public static final int TEXTURE_2D_MULTISAMPLE = GL_TEXTURE_2D_MULTISAMPLE;
	public static final int PROXY_TEXTURE_2D_MULTISAMPLE = GL_PROXY_TEXTURE_2D_MULTISAMPLE;

	// Some formats, included here for convenience
	public static final int RED = GL_RED;
	public static final int RG = GL_RG;
	public static final int RGB = GL_RGB;
	public static final int BGR = GL_BGR;
	public static final int RGBA = GL_RGBA;
	public static final int BGRA = GL_BGRA;
	public static final int RED_INTEGER = GL_RED_INTEGER;
	public static final int RG_INTEGER = GL_RG_INTEGER;
	public static final int RGB_INTEGER = GL_RGB_INTEGER;
	public static final int BGR_INTEGER = GL_BGR_INTEGER;
	public static final int RGBA_INTEGER = GL_RGBA_INTEGER;
	public static final int BGRA_INTEGER = GL_BGRA_INTEGER;
	public static final int STENCIL_INDEX = GL_STENCIL_INDEX;
	public static final int DEPTH_COMPONENT = GL_DEPTH_COMPONENT;
	public static final int DEPTH_STENCIL = GL_DEPTH_STENCIL;

	// Some types, included here for convenience
	public static final int UNSIGNED_BYTE = GL_UNSIGNED_BYTE;
	public static final int BYTE = GL_BYTE;
	public static final int UNSIGNED_SHORT = GL_UNSIGNED_SHORT;
	public static final int SHORT = GL_SHORT;
	public static final int UNSIGNED_INT = GL_UNSIGNED_INT;
	public static final int INT = GL_INT;
	public static final int HALF_FLOAT = GL_HALF_FLOAT;
	public static final int FLOAT = GL_FLOAT;
	public static final int UNSIGNED_BYTE_3_3_2 = GL_UNSIGNED_BYTE_3_3_2;
	public static final int UNSIGNED_BYTE_2_3_3_REV = GL_UNSIGNED_BYTE_2_3_3_REV;
	public static final int UNSIGNED_SHORT_5_6_5 = GL_UNSIGNED_SHORT_5_6_5;
	public static final int UNSIGNED_SHORT_5_6_5_REV = GL_UNSIGNED_SHORT_5_6_5_REV;
	public static final int UNSIGNED_SHORT_4_4_4_4 = GL_UNSIGNED_SHORT_4_4_4_4;
	public static final int UNSIGNED_SHORT_4_4_4_4_REV = GL_UNSIGNED_SHORT_4_4_4_4_REV;
	public static final int UNSIGNED_SHORT_5_5_5_1 = GL_UNSIGNED_SHORT_5_5_5_1;
	public static final int UNSIGNED_SHORT_1_5_5_5_REV = GL_UNSIGNED_SHORT_1_5_5_5_REV;
	public static final int UNSIGNED_INT_8_8_8_8 = GL_UNSIGNED_INT_8_8_8_8;
	public static final int UNSIGNED_INT_8_8_8_8_REV = GL_UNSIGNED_INT_8_8_8_8_REV;
	public static final int UNSIGNED_INT_10_10_10_2 = GL_UNSIGNED_INT_10_10_10_2;
	public static final int UNSIGNED_INT_2_10_10_10_REV = GL_UNSIGNED_INT_2_10_10_10_REV;

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
			NONE.load(buffer, 1, 1, RGBA);
		}
	}

	public final String name;
	private int id;
	private int target;
	private int width;
	private int height;

	private TextureAnimation animation;

	public Texture() {
		this("");
	}

	public Texture(String name) {
		this.name = name;
	}

	@Override
	public final void close() {
		glDeleteBuffers(id);
		id = 0;
	}

	protected void init(int width, int height) {
		close();
		this.id = glGenTextures();
		this.width = width;
		this.height = height;
	}

	public final void load(BufferedImage image) {
		init(image.getWidth(), image.getHeight());
		target = TEXTURE_2D;
		bind();
		glTexImage2D(target, 0, GL_RGBA8, width, height, 0, RGBA, UNSIGNED_BYTE, buildBuffer(image));
		setDefaultParameter();
		unbind();
	}

	public final void load(Image image) {
		init(image.width, image.height);
		target = TEXTURE_2D;
		bind();
		loadImage(image);
		setDefaultParameter();
		unbind();
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int format) {
		load(data, width, height, format, format);
	}

	public final void load(@Nullable ByteBuffer data, int width, int height, int format, int internalformat) {
		init(width, height);
		target = TEXTURE_2D;
		bind();
		glTexImage2D(target, 0, internalformat, width, height, 0, format, UNSIGNED_BYTE, data);
		setDefaultParameter();
		unbind();
	}

	public final void loadMultisample(int samples, int width, int height, int internalFormat) {
		loadMultisample(samples, width, height, internalFormat, true);
	}

	public final void loadMultisample(int samples, int width, int height, int internalFormat,
			boolean fixedsamplelocations) {
		init(width, height);
		target = TEXTURE_2D_MULTISAMPLE;
		bind();
		glTexImage2DMultisample(target, samples, internalFormat, width, height, fixedsamplelocations);
		setDefaultParameter();
		unbind();
	}

	private final void setDefaultParameter() {
		setWrap(REPEAT);
		setFilter(NEAREST);
		// glGenerateMipmap(GL_TEXTURE_2D);
	}

	public void setFilter(int filter) {
		setFilter(filter, filter);
	}

	public void setFilter(int minFilter, int magFilter) {
		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
	}

	public void setWrap(int wrap) {
		glTexParameteri(target, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(target, GL_TEXTURE_WRAP_T, wrap);
	}

	public final void setParameteri(int parameter, int value) {
		glTexParameteri(target, parameter, value);
	}

	public final void setParameterf(int parameter, int value) {
		glTexParameterf(target, parameter, value);
	}

	public final void setParameteriv(int parameter, int... values) {
		glTexParameteriv(target, parameter, values);
	}

	public final void setParameteriv(int parameter, IntBuffer values) {
		glTexParameteriv(target, parameter, values);
	}

	@Override
	public final void bind() {
		glBindTexture(target, id);
	}

	public final void bind(int unit, int level, boolean layered, int layer, int access, int format) {
		glBindImageTexture(unit, id, level, layered, layer, access, format);
	}

	@Override
	public final void unbind() {
		glBindTexture(target, 0);
	}

	public final void update(int xOffset, int yOffset, ByteBuffer buffer, int format) {
		glTexSubImage2D(target, 0, xOffset, yOffset, width, height, format, UNSIGNED_BYTE, buffer);
	}

	public final int getId() {
		return id;
	}

	public int getTarget() {
		return target;
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

	public final void setAnimation(TextureAnimation animation) {
		this.animation = animation;
	}

	public final TextureAnimation getAnimation() {
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
			format = RGB;
		} else {
			premultiplyAlpha(image);
			format = RGBA;
		}

		glTexImage2D(TEXTURE_2D, 0, format, image.width, image.height, 0, format, UNSIGNED_BYTE, image.data);

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

			glTexImage2D(TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, UNSIGNED_BYTE,
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
