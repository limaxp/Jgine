package org.jgine.render.graphic.text;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.FileUtils;
import org.jgine.misc.utils.logger.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

public class TrueTypeFont implements Font {

	public static final int MAX_SIZE = 1000;
	private static final Map<String, TrueTypeFont> NAME_MAP = new HashMap<String, TrueTypeFont>();
	private static final TrueTypeFont[] ID_MAP = new TrueTypeFont[MAX_SIZE];
	private static int increment;

	public static TrueTypeFont ARIAL = TrueTypeFont.load("arial", FileUtils.getResourceStream("fonts/arial/ARIAL.TTF"));

	public final int id;
	public final String name;
	protected final ByteBuffer ttf;
	public final STBTTFontinfo info;
	public final int ascent;
	public final int descent;
	public final int lineGap;

	public TrueTypeFont(String name, ByteBuffer ttf) {
		this.id = increment++;
		this.name = name;
		this.ttf = ttf;
		info = STBTTFontinfo.create();
		if (!stbtt_InitFont(info, ttf))
			throw new IllegalStateException("Failed to initialize font information.");

		try (MemoryStack stack = stackPush()) {
			IntBuffer pAscent = stack.mallocInt(1);
			IntBuffer pDescent = stack.mallocInt(1);
			IntBuffer pLineGap = stack.mallocInt(1);
			stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);
			ascent = pAscent.get(0);
			descent = pDescent.get(0);
			lineGap = pLineGap.get(0);
		}
		NAME_MAP.put(name, this);
		ID_MAP[id] = this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Nullable
	public static TrueTypeFont load(String name, File file) {
		try {
			return new TrueTypeFont(name, FileUtils.readByteBuffer(file));
		} catch (IOException e) {
			Logger.err("TrueTypeFont: Error loading file '" + file.getPath() + "'", e);
			return null;
		}
	}

	@Nullable
	public static TrueTypeFont load(String name, InputStream is) {
		try {
			return new TrueTypeFont(name, FileUtils.readByteBuffer(is));
		} catch (IOException e) {
			Logger.err("TrueTypeFont: Error loading input stream", e);
			return null;
		}
	}

	@Nullable
	public static TrueTypeFont get(String name) {
		return NAME_MAP.get(name);
	}

	@Nullable
	public static TrueTypeFont get(int id) {
		return ID_MAP[id];
	}
}
