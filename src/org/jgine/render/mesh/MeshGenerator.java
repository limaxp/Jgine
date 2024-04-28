package org.jgine.render.mesh;

import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.core.window.DisplayManager;
import org.jgine.render.mesh.text.BitmapFont;
import org.jgine.render.mesh.text.Text;
import org.jgine.render.mesh.text.TrueTypeFont;
import org.jgine.render.mesh.text.TrueTypeFontGenerated;
import org.jgine.render.mesh.text.TrueTypeText;
import org.jgine.utils.Options;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.vector.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

public class MeshGenerator {

	public static BaseMesh quad(float size) {
		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(new float[] { -size, size, size, size, -size, -size, size, -size },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		mesh.mode = Mesh.TRIANGLE_STRIP;
		return mesh;
	}

	public static BaseMesh quadHollow(float size) {
		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(new float[] { -size, -size, size, -size, size, size, -size, size },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
		mesh.mode = Mesh.LINE_LOOP;
		return mesh;
	}

	public static Mesh cube(float size) {
		Mesh mesh = new Mesh(3, true);
		mesh.loadVertices(
				new float[] { size, size, size, -size, size, size, size, size, -size, -size, size, -size, size, -size,
						size, -size, -size, size, size, -size, -size, -size, -size, -size },
				new int[] { 2, 1, 0, 3, 1, 2, 0, 1, 4, 5, 4, 1, 6, 3, 2, 6, 7, 3, 1, 3, 5, 5, 3, 7, 0, 4, 6, 0, 6, 2, 4,
						5, 6, 7, 6, 5, },
				null, null);
		return mesh;
	}

	public static BaseMesh hexagon(float radius) {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(32);
		float radiusHalf = radius * 0.5f;
		addVertice(vertices, 0.0f, 0.0f); // center
		addVertice(vertices, -radiusHalf, radius); // left top
		addVertice(vertices, radiusHalf, radius); // right top
		addVertice(vertices, radius, 0.0f); // right
		addVertice(vertices, radiusHalf, -radius); // right bottom
		addVertice(vertices, -radiusHalf, -radius); // left bottom
		addVertice(vertices, -radius, 0.0f); // left
		addVertice(vertices, -radiusHalf, radius); // left top
		vertices.flip();

		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(vertices);
		mesh.mode = Mesh.TRIANGLE_FAN;
		return mesh;
	}

	public static BaseMesh circle(float radius, int steps) {
		FloatBuffer vertices = BufferUtils.createFloatBuffer((steps + 3) * 4);
		addVertice(vertices, 0.0f, 0.0f);
		addVertice(vertices, radius * FastMath.sin(0), radius * FastMath.cos(0));
		float angle = (float) FastMath.PI2 / steps;
		for (float phi = angle; phi < FastMath.PI2; phi += angle)
			addVertice(vertices, radius * FastMath.sin(phi), radius * FastMath.cos(phi));
		addVertice(vertices, radius * FastMath.sin(0), radius * FastMath.cos(0));
		vertices.flip();

		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(vertices);
		mesh.mode = Mesh.TRIANGLE_FAN;
		return mesh;
	}

	public static BaseMesh circleHollow(float radius, int steps) {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(steps * 4);
		addVertice(vertices, radius * FastMath.sin(0), radius * FastMath.cos(0));
		float angle = (float) FastMath.PI2 / steps;
		for (float phi = angle; phi < FastMath.PI2; phi += angle)
			addVertice(vertices, radius * FastMath.sin(phi), radius * FastMath.cos(phi));
		vertices.flip();

		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(vertices);
		mesh.mode = Mesh.LINE_LOOP;
		return mesh;
	}

	public static BaseMesh line(float x1, float y1, float x2, float y2) {
		BaseMesh mesh = new BaseMesh(2, false);
		mesh.loadVertices(new float[] { x1, y1, x2, y2 }, null);
		mesh.mode = Mesh.LINES;
		return mesh;
	}

	public static BaseMesh line(float x1, float y1, float z1, float x2, float y2, float z2) {
		BaseMesh mesh = new BaseMesh(3, false);
		mesh.loadVertices(new float[] { x1, y1, z1, x2, y2, z2 }, null);
		mesh.mode = Mesh.LINES;
		return mesh;
	}

	public static BaseMesh line(int dimension, boolean loop, float[] points) {
		BaseMesh mesh = new BaseMesh(dimension, false);
		mesh.loadVertices(points, null);
		if (loop)
			mesh.mode = Mesh.LINE_LOOP;
		else
			mesh.mode = Mesh.LINE_STRIP;
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
					addVertice(vertices, x0, x0);
					addVertice(vertices, x0, x0);
					x0 = xOffset;
					y0 -= tileHeight * scale * factorY;
					addVertice(vertices, x0, x0);
					addVertice(vertices, x0, x0);
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
			BaseMesh mesh = new BaseMesh(2, false);
			mesh.loadVertices(vertices);
			mesh.mode = Mesh.TRIANGLE_STRIP;
			return mesh;
		}
	}

	public static BaseMesh text(TrueTypeFontGenerated generatedFont, int size, String text) {
		return text(generatedFont, size, text, 0.0f, 0.0f);
	}

	public static BaseMesh text(TrueTypeFontGenerated generatedFont, int size, String text, float xOffset,
			float yOffset) {
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
			BaseMesh mesh = new BaseMesh(2, false);
			mesh.loadVertices(vertices);
			return mesh;
		}
	}

	public static void addTriangleStrip(FloatBuffer vertices, float x0, float x1, float y0, float y1, float s0,
			float s1, float t0, float t1) {
		addVertice(vertices, x0, y0, s0, t1);
		addVertice(vertices, x0, y1, s0, t0);
		addVertice(vertices, x1, y0, s1, t1);
		addVertice(vertices, x1, y1, s1, t0);
	}

	public static void addQuad(FloatBuffer vertices, float x0, float x1, float y0, float y1, float s0, float s1,
			float t0, float t1) {
		addVertice(vertices, x0, y0, s0, t1);
		addVertice(vertices, x0, y1, s0, t0);
		addVertice(vertices, x1, y0, s1, t1);
		addVertice(vertices, x1, y0, s1, t1);
		addVertice(vertices, x0, y1, s0, t0);
		addVertice(vertices, x1, y1, s1, t0);
	}

	public static void addVertice(FloatBuffer vertices, float x, float y) {
		addVertice(vertices, x, y, 0.0f, 0.0f);
	}

	public static void addVertice(FloatBuffer vertices, float x, float y, float s, float t) {
		vertices.put(x);
		vertices.put(y);
		vertices.put(s);
		vertices.put(t);
	}

	public static void addVertice(FloatBuffer vertices, float x, float y, float z) {
		addVertice(vertices, x, y, z, 0.0f, 0.0f, 0.0f);
	}

	public static void addVertice(FloatBuffer vertices, float x, float y, float z, float s, float t, float v) {
		vertices.put(x);
		vertices.put(y);
		vertices.put(z);
		vertices.put(s);
		vertices.put(t);
		vertices.put(v);
	}

	public static float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}
}
