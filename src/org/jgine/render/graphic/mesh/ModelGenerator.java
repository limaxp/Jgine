package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.core.window.DisplayManager;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.graphic.text.BitmapFont;
import org.jgine.render.graphic.text.Text;
import org.jgine.render.graphic.text.TrueTypeFont;
import org.jgine.render.graphic.text.TrueTypeFontGenerated;
import org.jgine.render.graphic.text.TrueTypeText;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

public class ModelGenerator {

	public static BaseMesh quad(float size) {
		BaseMesh mesh = new BaseMesh();
		mesh.loadData(2, new float[] { -size, size, size, size, -size, -size, size, -size },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		mesh.mode = Mesh.TRIANGLE_STRIP;
		return mesh;
	}

	public static Mesh cube(float size) {
		Mesh mesh = new Mesh();
		mesh.loadData(3,
				new float[] { size, size, size, -size, size, size, size, size, -size, -size, size, -size, size, -size,
						size, -size, -size, size, size, -size, -size, -size, -size, -size },
				new int[] { 2, 1, 0, 3, 1, 2, 0, 1, 4, 5, 4, 1, 6, 3, 2, 6, 7, 3, 1, 3, 5, 5, 3, 7, 0, 4, 6, 0, 6, 2, 4,
						5, 6, 7, 6, 5, });
		return mesh;
	}
	
	public static BaseMesh text(BitmapFont font, int size, String text) {
		return text(font, size, text, 0.0f, 0.0f);
	}

	public static BaseMesh text(BitmapFont font, int size, String text, float xOffset, float yOffset) {
		try (MemoryStack stack = stackPush()) {
			int colums = font.colums;
			int rows = font.rows;
			float tileWidth = font.getTileWidth();
			float tileHeight = font.getTileHeight();
			FloatBuffer vertices = BufferUtils.createFloatBuffer(text.length() * 16);
			float x0 = xOffset;
			float y0 = yOffset;
			float scale = (float) size / 64;
			Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
			float factorX = 1.0f / contentScale.x;
			float factorY = 1.0f / contentScale.y;
			IntBuffer pCodePoint = stack.mallocInt(1);
			for (int i = 0, to = text.length(); i < to;) {
				i += Text.getCP(text, to, i, pCodePoint);
				int cp = pCodePoint.get(0);
				if (cp == '\n') {
					vertices.put(x0);
					vertices.put(y0);
					vertices.put(0);
					vertices.put(0);
					vertices.put(x0);
					vertices.put(y0);
					vertices.put(0);
					vertices.put(0);

					x0 = xOffset;
					y0 -= tileHeight * scale * factorY;

					vertices.put(x0);
					vertices.put(y0);
					vertices.put(0);
					vertices.put(0);
					vertices.put(x0);
					vertices.put(y0);
					vertices.put(0);
					vertices.put(0);
					continue;
				} else if (cp < 32 || 128 <= cp) {
					continue;
				}

				int col = cp % colums;
				int row = cp / colums;
				float x1 = x0 + tileWidth * scale * factorX;
				float y1 = y0 + tileHeight * scale * factorY;
				float s0 = (float) col / colums;
				float s1 = ((float) col + 1) / colums;
				float t0 = (float) row / rows;
				float t1 = ((float) row + 1) / rows;
				addTriangleStrip(vertices, x0, x1, y0, y1, s0, s1, t0, t1);
				x0 += tileWidth * scale * factorX;
			}
			vertices.flip();
			BaseMesh mesh = new BaseMesh();
			mesh.loadDataNoNormals(2, vertices);
			mesh.mode = GL_TRIANGLE_STRIP;
			return mesh;
		}
	}
	
	public static BaseMesh text(TrueTypeFontGenerated generatedFont, int size, String text) {
		return text(generatedFont, size, text, 0.0f, 0.0f);
	}

	public static BaseMesh text(TrueTypeFontGenerated generatedFont, int size, String text, float xOffset, float yOffset) {
		try (MemoryStack stack = stackPush()) {
			TrueTypeFont font = generatedFont.font;
			float scale = font.getScaleForPixelHeight(size);
			FloatBuffer vertices = BufferUtils.createFloatBuffer(text.length() * 24);
			IntBuffer pCodePoint = stack.mallocInt(1);
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);
			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

			Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
			float factorX = 1.0f / contentScale.x;
			float factorY = 1.0f / contentScale.y;
			float lineY = yOffset;
			x.put(0, xOffset);
			y.put(0, lineY);

			STBTTPackedchar.Buffer buffer = generatedFont.getBuffer(size);
			for (int i = 0, to = text.length(); i < to;) {
				i += Text.getCP(text, to, i, pCodePoint);

				int cp = pCodePoint.get(0);
				if (cp == '\n') {
					x.put(0, xOffset);
					y.put(0, lineY = y.get(0) + (font.ascent - font.descent + font.lineGap) * scale);
					continue;
				} else if (cp < 32 || 128 <= cp) {
					continue;
				}

				float cpX = x.get(0);
				stbtt_GetPackedQuad(buffer, generatedFont.width, generatedFont.height, cp, x, y, q, true);
				x.put(0, scale(cpX, x.get(0), factorX));
				if (TrueTypeText.IS_KERNING && i < to) {
					Text.getCP(text, to, i, pCodePoint);
					x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(font.info, cp, pCodePoint.get(0)) * scale);
				}

				float x0 = scale(cpX, q.x0(), factorX);
				float x1 = scale(cpX, q.x1(), factorX);
				float y0 = scale(lineY, -q.y1(), factorY);
				float y1 = scale(lineY, -q.y0(), factorY);
				addQuad(vertices, x0, x1, y0, y1, q.s0(), q.s1(), q.t0(), q.t1());
			}
			vertices.flip();
			BaseMesh mesh = new BaseMesh();
			mesh.loadDataNoNormals(2, vertices);
			return mesh;
		}
	}
	
	public static void addTriangleStrip(FloatBuffer vertices, float x0, float x1, float y0, float y1, float s0, float s1,
			float t0, float t1) {
		vertices.put(x0);
		vertices.put(y0);
		vertices.put(s0);
		vertices.put(t1);

		vertices.put(x0);
		vertices.put(y1);
		vertices.put(s0);
		vertices.put(t0);

		vertices.put(x1);
		vertices.put(y0);
		vertices.put(s1);
		vertices.put(t1);

		vertices.put(x1);
		vertices.put(y1);
		vertices.put(s1);
		vertices.put(t0);
	}

	public static void addQuad(FloatBuffer vertices, float x0, float x1, float y0, float y1, float s0, float s1,
			float t0, float t1) {
		vertices.put(x0);
		vertices.put(y0);
		vertices.put(s0);
		vertices.put(t1);

		vertices.put(x0);
		vertices.put(y1);
		vertices.put(s0);
		vertices.put(t0);

		vertices.put(x1);
		vertices.put(y0);
		vertices.put(s1);
		vertices.put(t1);

		vertices.put(x1);
		vertices.put(y0);
		vertices.put(s1);
		vertices.put(t1);

		vertices.put(x0);
		vertices.put(y1);
		vertices.put(s0);
		vertices.put(t0);

		vertices.put(x1);
		vertices.put(y1);
		vertices.put(s1);
		vertices.put(t0);
	}

	public static float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}
}
