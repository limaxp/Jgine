package org.jgine.render.graphic.text;

import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.core.window.DisplayManager;
import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

public class TrueTypeText extends Text implements AutoCloseable {

	public static boolean IS_KERNING = true;

	public TrueTypeText(TrueTypeFont font, int size, String text) {
		super(font, size, text, new Material());
		buildMesh();
	}
	
	protected void buildMesh() {
		if (mesh != null)
			close();
		TrueTypeFontGenerated generatedFont = TrueTypeFontGenerated.get((TrueTypeFont) font);
		material.setTexture(generatedFont.texture);
		mesh = buildMesh(generatedFont, size, text);
	}
	
	@Override
	public int getType() {
		return TYPE_TRUETYPE;
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

	public static BaseMesh buildMesh(TrueTypeFontGenerated generatedFont, int size, String text) {
		try (MemoryStack stack = stackPush()) {
			TrueTypeFont font = generatedFont.font;
			float scale = font.getScaleForPixelHeight(size);
			FloatList positions = new FloatArrayList();
			FloatList textCoords = new FloatArrayList();
			IntBuffer pCodePoint = stack.mallocInt(1);
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);
			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

			Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
			float factorX = 1.0f / contentScale.x;
			float factorY = 1.0f / contentScale.y;
			float lineY = 0.0f;

			STBTTPackedchar.Buffer buffer = generatedFont.getBuffer(size);
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
				stbtt_GetPackedQuad(buffer, generatedFont.width, generatedFont.height, cp, x, y, q, true);
				x.put(0, scale(cpX, x.get(0), factorX));
				if (IS_KERNING && i < to) {
					getCP(text, to, i, pCodePoint);
					x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(font.info, cp, pCodePoint.get(0)) * scale);
				}

				float x0 = scale(cpX, q.x0(), factorX);
				float x1 = scale(cpX, q.x1(), factorX);
				float y0 = scale(lineY, -q.y1(), factorY);
				float y1 = scale(lineY, -q.y0(), factorY);

				positions.add(x0);
				positions.add(y0);
				textCoords.add(q.s0());
				textCoords.add(q.t1());

				positions.add(x0);
				positions.add(y1);
				textCoords.add(q.s0());
				textCoords.add(q.t0());

				positions.add(x1);
				positions.add(y0);
				textCoords.add(q.s1());
				textCoords.add(q.t1());

				positions.add(x1);
				positions.add(y0);
				textCoords.add(q.s1());
				textCoords.add(q.t1());

				positions.add(x0);
				positions.add(y1);
				textCoords.add(q.s0());
				textCoords.add(q.t0());

				positions.add(x1);
				positions.add(y1);
				textCoords.add(q.s1());
				textCoords.add(q.t0());
			}
			BaseMesh mesh = new BaseMesh();
			mesh.loadData(2, positions.toFloatArray(), textCoords.toFloatArray());
			return mesh;
		}
	}

	private static float scale(float center, float offset, float factor) {
		return (offset - center) * factor + center;
	}
}
