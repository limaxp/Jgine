package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

import java.nio.charset.Charset;

import org.jgine.core.window.DisplayManager;
import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh2D;

public class BitmapText extends Text {

	public BitmapText(BitmapFont font, int size, String text) {
		super(font, size, text, new Material(font.texture));
		buildMesh();
	}

	protected void buildMesh() {
		if (mesh != null)
			close();
		mesh = buildMesh((BitmapFont) font, size, text);
	}

	@Override
	public void setFont(Font font) {
		if (!(font instanceof BitmapFont))
			return;
		super.setFont(font);
		BitmapFont bitmapFont = (BitmapFont) font;
		BitmapFont currentFont = getFont();
		this.material.setTexture(bitmapFont.texture);
		if (currentFont.colums != bitmapFont.colums || currentFont.rows != bitmapFont.rows)
			buildMesh();
	}

	@Override
	public BitmapFont getFont() {
		return (BitmapFont) font;
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

	public static BaseMesh2D buildMesh(BitmapFont font, int size, String text) {
		// TODO size!
		int colums = font.colums;
		int rows = font.rows;
		float tileWidth = font.getTileWidth();
		float tileHeight = font.getTileHeight();
		FloatList positions = new FloatArrayList();
		FloatList textCoords = new FloatArrayList();
		byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
		int numChars = chars.length;
		float x0 = 0;
		float y0 = 0;
		Vector2f contentScale = DisplayManager.getDisplay(Options.MONITOR.getInt()).getContentScale();
		float factorX = 1.0f / contentScale.x;
		float factorY = 1.0f / contentScale.y;
		for (int i = 0; i < numChars; i++) {
			byte currChar = chars[i];
			if (currChar == '\n') {
				positions.add(x0);
				positions.add(y0);
				textCoords.add(0);
				textCoords.add(0);
				positions.add(x0);
				positions.add(y0);
				textCoords.add(0);
				textCoords.add(0);
				
				x0 = 0;
				y0 -= tileHeight * factorY;
				
				positions.add(x0);
				positions.add(y0);
				textCoords.add(0);
				textCoords.add(0);
				positions.add(x0);
				positions.add(y0);
				textCoords.add(0);
				textCoords.add(0);
				continue;
			}

			int col = currChar % colums;
			int row = currChar / colums;
			float x1 = x0 + tileWidth * factorX;
			float y1 = y0 + tileHeight * factorY;
			float s0 = (float) col / colums;
			float s1 = ((float) col + 1) / colums;
			float t0 = (float) row /  rows;
			float t1 = ((float) row + 1) / rows;
			
			positions.add(x0);
			positions.add(y0);
			textCoords.add(s0);
			textCoords.add(t1);

			positions.add(x0);
			positions.add(y1);
			textCoords.add(s0);
			textCoords.add(t0);

			positions.add(x1);
			positions.add(y0);
			textCoords.add(s1);
			textCoords.add(t1);

			positions.add(x1);
			positions.add(y1);
			textCoords.add(s1);
			textCoords.add(t0);
			x0 += tileWidth * factorX;
		}
		BaseMesh2D mesh = new BaseMesh2D(positions.toFloatArray(), textCoords.toFloatArray());
		mesh.setMode(GL_TRIANGLE_STRIP);
		return mesh;
	}
}