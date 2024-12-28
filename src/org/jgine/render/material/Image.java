package org.jgine.render.material;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_is_hdr_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.FileUtils;
import org.jgine.utils.logger.Logger;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

public class Image {

	public final ByteBuffer data;
	public final int width;
	public final int height;
	public final int components;
	public final boolean isHDR;

	private Image(ByteBuffer data, int width, int height, int components, boolean isHDR) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.components = components;
		this.isHDR = isHDR;
	}

	@Nullable
	public static Image read(File file) {
		ByteBuffer imageBuffer;
		try {
			imageBuffer = FileUtils.readByteBuffer(file);
		} catch (IOException e) {
			Logger.err("Image: Error an reading file byte buffer!", e);
			return null;
		}
		return read(imageBuffer);
	}

	@Nullable
	public static Image read(InputStream is) {
		ByteBuffer imageBuffer;
		try {
			imageBuffer = FileUtils.readByteBuffer(is);
		} catch (IOException e) {
			Logger.err("Image: Error an reading stream byte buffer!", e);
			return null;
		}
		return read(imageBuffer);
	}

	@Nullable
	public static Image read(ByteBuffer imageBuffer) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Use info to read image metadata without decoding the entire image.
			// We don't need this for this demo, just testing the API.
			if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
				Logger.warn("Failed to read image information: " + stbi_failure_reason());
				return null;
			}

			// Decode the image
			ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			if (image == null) {
				Logger.warn("Failed to load image: " + stbi_failure_reason());
				return null;
			}
			return new Image(image, w.get(0), h.get(0), comp.get(0), stbi_is_hdr_from_memory(imageBuffer));
		}
	}

	public static void writePNG(String path, Image image) {
		STBImageWrite.stbi_write_png(path, image.width, image.height, image.components, image.data, 0);
	}

	public static void writePNG(String path, Image image, int stride) {
		STBImageWrite.stbi_write_png(path, image.width, image.height, image.components, image.data, stride);
	}

	public static void writePNG(String path, int width, int height, int components, ByteBuffer data, int stride) {
		STBImageWrite.stbi_write_png(path, width, height, components, data, stride);
	}

	public static void writeJPG(String path, Image image) {
		STBImageWrite.stbi_write_jpg(path, image.width, image.height, image.components, image.data, 0);
	}

	public static void writeJPG(String path, Image image, int stride) {
		STBImageWrite.stbi_write_jpg(path, image.width, image.height, image.components, image.data, stride);
	}

	public static void writeJPG(String path, int width, int height, int components, ByteBuffer data, int stride) {
		STBImageWrite.stbi_write_jpg(path, width, height, components, data, stride);
	}
}
