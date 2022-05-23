package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_SWIZZLE_RGBA;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.graphic.mesh.BaseMesh2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

public class TrueTypeText extends Text implements AutoCloseable {

	private TrueTypeFont font;

	public TrueTypeText(TrueTypeFont font, String text) {
		super(text, new Material());
		this.font = font;
		buildMesh(font);
	}

	public static final int FONT_HEIGHT = 64;
	public static final int CONTENT_SCALE = 1;
	public static boolean IS_KERNING = true;

	private STBTTBakedChar.Buffer buildTexture(TrueTypeFont font, int BITMAP_W, int BITMAP_H) {
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
		stbtt_BakeFontBitmap(font.ttf, FONT_HEIGHT * CONTENT_SCALE, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

		Texture texture = new Texture();
		texture.load(bitmap, BITMAP_W, BITMAP_H, GL11.GL_RED);
		texture.setParameteriv(GL_TEXTURE_SWIZZLE_RGBA, new int[] { GL_ZERO, GL_ZERO, GL_ZERO, GL_RED });
		material.setTexture(texture);
		return cdata;
	}

	private void buildMesh(TrueTypeFont font) {
		if (mesh != null)
			close();
		int BITMAP_W = FastMath.round(512 * CONTENT_SCALE);
		int BITMAP_H = FastMath.round(512 * CONTENT_SCALE);
		STBTTBakedChar.Buffer cdata = buildTexture(font, BITMAP_W, BITMAP_H);
		float scale = stbtt_ScaleForPixelHeight(font.info, FONT_HEIGHT);
		FloatList positions = new FloatArrayList();
		FloatList textCoords = new FloatArrayList();

		try (MemoryStack stack = stackPush()) {
			IntBuffer pCodePoint = stack.mallocInt(1);
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);
			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

			float factorX = 1.0f / CONTENT_SCALE;
			float factorY = 1.0f / CONTENT_SCALE;
			float lineY = 0.0f;

			for (int i = 0, to = text.length(); i < to;) {
				i += getCP(text, to, i, pCodePoint);

				int cp = pCodePoint.get(0);
				if (cp == '\n') {
					y.put(0, lineY = y.get(0) + (font.ascent - font.descent + font.lineGap) * scale);
					x.put(0, 0.0f);
					continue;
				} else if (cp < 32 || 128 <= cp) {
					continue;
				}

				float cpX = x.get(0);
				stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);
				x.put(0, scale(cpX, x.get(0), factorX));
				if (IS_KERNING && i < to) {
					getCP(text, to, i, pCodePoint);
					x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(font.info, cp, pCodePoint.get(0)) * scale);
				}

				float x0 = scale(cpX, q.x0(), factorX), x1 = scale(cpX, q.x1(), factorX),
						y0 = scale(lineY, -q.y1(), factorY), y1 = scale(lineY, -q.y0(), factorY);

				// Left Top vertex
				positions.add(x0);
				positions.add(y1);
				textCoords.add(q.s0());
				textCoords.add(q.t0());

				// Left Bottom vertex
				positions.add(x1);
				positions.add(y1);
				textCoords.add(q.s1());
				textCoords.add(q.t0());

				// Right Bottom vertex
				positions.add(x0);
				positions.add(y0);
				textCoords.add(q.s0());
				textCoords.add(q.t1());

				// Right Top vertex
				positions.add(x1);
				positions.add(y0);
				textCoords.add(q.s1());
				textCoords.add(q.t1());
			}
		}
		mesh = new BaseMesh2D(positions.toFloatArray(), textCoords.toFloatArray());
		mesh.setMode(GL_TRIANGLE_STRIP);
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		close();
		buildMesh(font);
	}

	@Override
	public void setFont(Font font) {
		// if (this.font.colums != font.colums || this.font.rows != font.rows)
		// buildMesh(font);
		// this.material.setTexture(font);
		// this.font = font;
	}

	@Override
	public TrueTypeFont getFont() {
		return font;
	}

	private static float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}

	private static int getCP(String text, int to, int i, IntBuffer cpOut) {
		char c1 = text.charAt(i);
		if (Character.isHighSurrogate(c1) && i + 1 < to) {
			char c2 = text.charAt(i + 1);
			if (Character.isLowSurrogate(c2)) {
				cpOut.put(0, Character.toCodePoint(c1, c2));
				return 2;
			}
		}
		cpOut.put(0, c1);
		return 1;
	}
}
