package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_SWIZZLE_RGBA;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
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
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;

public class TrueTypeFontGenerated implements AutoCloseable {

	private static final int RESOLUTION = 512;
	private static final int CHACHE_SIZE = 32;
	private static final TrueTypeFontGenerated[] CACHE = new TrueTypeFontGenerated[CHACHE_SIZE];
	private static int cacheSize;
	private static final Map<Long, TrueTypeFontGenerated> ID_MAP = new ConcurrentHashMap<Long, TrueTypeFontGenerated>();

	public final TrueTypeFont font;
	public final int size;
	public final STBTTBakedChar.Buffer buffer;
	public final Texture texture;
	public final int width;
	public final int height;

	protected TrueTypeFontGenerated(TrueTypeFont font, int size, int width, int height) {
		this.font = font;
		this.size = size;
		Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
		this.width = width = FastMath.round(width * contentScale.x);
		this.height = height = FastMath.round(height * contentScale.y);
		buffer = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(width * height);
		stbtt_BakeFontBitmap(font.ttf, size * contentScale.y, bitmap, width, height, 32, buffer);
		texture = new Texture();
		texture.load(bitmap, width, height, GL11.GL_RED);
		texture.setParameteriv(GL_TEXTURE_SWIZZLE_RGBA, new int[] { GL_ZERO, GL_ZERO, GL_ZERO, GL_RED });
	}

	@Override
	public void close() {
		buffer.free();
		texture.close();
	}

	public float getScaleForPixelHeight() {
		return stbtt_ScaleForPixelHeight(font.info, size);
	}

	public static TrueTypeFontGenerated get(TrueTypeFont font, int size) {
		long id = getId(font, size);
		TrueTypeFontGenerated result = ID_MAP.get(id);
		if (result == null) {
			result = new TrueTypeFontGenerated(font, size, RESOLUTION, RESOLUTION);
			if (cacheSize >= CHACHE_SIZE)
				cacheSize = 0;
			TrueTypeFontGenerated old = CACHE[cacheSize];
			if (old != null) {
				old.close();
				ID_MAP.remove(getId(old.font, old.size));
			}
			CACHE[cacheSize++] = result;
			ID_MAP.put(id, result);
		}
		return result;
	}

	private static long getId(TrueTypeFont font, int size) {
		return 0x00000000 | font.id << 32 | size;
	}
}
