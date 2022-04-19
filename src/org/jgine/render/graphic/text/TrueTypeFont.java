package org.jgine.render.graphic.text;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jgine.misc.utils.FileUtils;
import org.jgine.misc.utils.logger.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

public class TrueTypeFont implements Font {

	public static TrueTypeFont CONSOLAS = TrueTypeFont.create(getResourceFile("fonts/arial/ARIAL.TTF"));

	public static InputStream getResourceFile(String name) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if (is == null) {
			Logger.warn("ResourceLoader: resource not found: " + name);
			return is;
		}
		return is;
	}

	protected final ByteBuffer ttf;
	public final STBTTFontinfo info;
	public final int ascent;
	public final int descent;
	public final int lineGap;

	public static TrueTypeFont create(File file) {
		try {
			return new TrueTypeFont(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static TrueTypeFont create(InputStream is) {
		try {
			return new TrueTypeFont(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public TrueTypeFont(File file) throws IOException {
		this(FileUtils.readByteBuffer(file));
	}

	public TrueTypeFont(InputStream is) throws IOException {
		this(FileUtils.readByteBuffer(is));
	}

	public TrueTypeFont(ByteBuffer ttf) {
		this.ttf = ttf;
		info = STBTTFontinfo.create();
		if (!stbtt_InitFont(info, ttf)) {
			throw new IllegalStateException("Failed to initialize font information.");
		}

		try (MemoryStack stack = stackPush()) {
			IntBuffer pAscent = stack.mallocInt(1);
			IntBuffer pDescent = stack.mallocInt(1);
			IntBuffer pLineGap = stack.mallocInt(1);

			stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

			ascent = pAscent.get(0);
			descent = pDescent.get(0);
			lineGap = pLineGap.get(0);
		}
	}
}
