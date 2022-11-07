package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh2D;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

public class TrueTypeText extends Text implements AutoCloseable {

	public static final int CONTENT_SCALE = 1;
	public static boolean IS_KERNING = true;

	public TrueTypeText(TrueTypeFont font, int size, String text) {
		super(font, size, text, new Material());
		this.font = font;
		buildMesh();
	}

	protected void buildMesh() {
		if (mesh != null)
			close();
		TrueTypeFontGenerated generatedFont = TrueTypeFontGenerated.get((TrueTypeFont) font, size);
		material.setTexture(generatedFont.texture);
		mesh = buildMesh(generatedFont, text);
	}

	@Override
	public void setFont(Font font) {
		if (!(font instanceof TrueTypeFont) || font == getFont())
			return;
		super.setFont(font);
		buildMesh();
	}

	@Override
	public TrueTypeFont getFont() {
		return (TrueTypeFont) font;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		buildMesh();
	}

	@Override
	public void setSize(int size) {
		super.setSize(size);
		buildMesh();
	}

	public static BaseMesh2D buildMesh(TrueTypeFontGenerated generatedFont, String text) {
		TrueTypeFont font = generatedFont.font;
		float scale = generatedFont.getScaleForPixelHeight();
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
				stbtt_GetBakedQuad(generatedFont.buffer, generatedFont.width, generatedFont.height, cp - 32, x, y, q,
						true);
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
		BaseMesh2D mesh = new BaseMesh2D(positions.toFloatArray(), textCoords.toFloatArray());
		mesh.setMode(GL_TRIANGLE_STRIP);
		return mesh;
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
