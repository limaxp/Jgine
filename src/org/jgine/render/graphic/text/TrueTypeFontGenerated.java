package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_SWIZZLE_RGBA;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jgine.core.window.DisplayManager;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.graphic.material.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

public class TrueTypeFontGenerated implements AutoCloseable {

	private static final int RESOLUTION = 4096;
	private static final int CHACHE_SIZE = 16;
	private static final TrueTypeFontGenerated[] CACHE = new TrueTypeFontGenerated[CHACHE_SIZE];
	private static int cacheSize;
	private static final Map<TrueTypeFont, TrueTypeFontGenerated> MAP = new ConcurrentHashMap<TrueTypeFont, TrueTypeFontGenerated>();

	private static final int[] SCALES = { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 20, 22, 24, 26, 28, 32, 36, 40,
			44, 48, 54, 60, 66, 72, 80, 88, 96 };

	public final TrueTypeFont font;
	public final STBTTPackedchar.Buffer buffer;
	public final Texture texture;
	public final int width;
	public final int height;

	protected TrueTypeFontGenerated(TrueTypeFont font) {
		this.font = font;
		Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
		this.width = FastMath.round(RESOLUTION * contentScale.x);
		this.height = FastMath.round(RESOLUTION * contentScale.y);

		buffer = STBTTPackedchar.malloc(SCALES.length * 3 * 128);
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			ByteBuffer bitmap = BufferUtils.createByteBuffer(width * height);

			stbtt_PackBegin(pc, bitmap, width, height, 0, 1, 0);
			for (int i = 0; i < SCALES.length; i++) {
				int p = (i * 3 + 0) * 128 + 32;
				buffer.limit(p + 95);
				buffer.position(p);
				stbtt_PackSetOversampling(pc, 1, 1);
				stbtt_PackFontRange(pc, font.ttf, 0, SCALES[i], 32, buffer);

				p = (i * 3 + 1) * 128 + 32;
				buffer.limit(p + 95);
				buffer.position(p);
				stbtt_PackSetOversampling(pc, 2, 2);
				stbtt_PackFontRange(pc, font.ttf, 0, SCALES[i], 32, buffer);

				p = (i * 3 + 2) * 128 + 32;
				buffer.limit(p + 95);
				buffer.position(p);
				stbtt_PackSetOversampling(pc, 3, 1);
				stbtt_PackFontRange(pc, font.ttf, 0, SCALES[i], 32, buffer);
			}
			buffer.clear();
			stbtt_PackEnd(pc);

			texture = new Texture();
			texture.load(bitmap, width, height, GL_RED);
			texture.bind();
			texture.setParameteriv(GL_TEXTURE_SWIZZLE_RGBA, new int[] { GL_ZERO, GL_ZERO, GL_ZERO, GL_RED });
			texture.setFilter(Texture.LINEAR);
			texture.unbind();
		}
	}

	@Override
	public void close() {
		buffer.free();
		texture.close();
	}

	public float getScaleForPixelHeight(int size) {
		return stbtt_ScaleForPixelHeight(font.info, size);
	}

	public STBTTPackedchar.Buffer getBuffer(int size) {
		STBTTPackedchar.Buffer result = STBTTPackedchar.create(buffer.address(), buffer.capacity());
		result.position(getBufferPosition(size));
		return result;
	}

	public static int getBufferPosition(int size) {
		return getSizeIndex(size) * 3 * 128;
	}

	public static int getSizeIndex(int size) {
		if (size < SCALES[0])
			return SCALES[0];
		if (size > SCALES[SCALES.length - 1])
			return SCALES[SCALES.length - 1];
		int smallest = Integer.MAX_VALUE;
		int smallestIndex = 0;
		for (int i = 1; i < SCALES.length - 1; i++) {
			int diff = FastMath.abs(SCALES[i] - size);
			if (diff <= 0)
				return i;
			if (diff < smallest) {
				smallest = diff;
				smallestIndex = i;
			}
		}
		return smallestIndex;
	}

	public static TrueTypeFontGenerated get(TrueTypeFont font) {
		TrueTypeFontGenerated result = MAP.get(font);
		if (result == null) {
			result = new TrueTypeFontGenerated(font);
			synchronized (CACHE) {
				if (cacheSize >= CHACHE_SIZE)
					cacheSize = 0;
				TrueTypeFontGenerated old = CACHE[cacheSize];
				if (old != null) {
					old.close();
					MAP.remove(old.font);
				}
				CACHE[cacheSize++] = result;
			}
			MAP.put(font, result);
		}
		return result;
	}
}
