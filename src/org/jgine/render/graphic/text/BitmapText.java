package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

import java.nio.charset.Charset;

import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.graphic.mesh.BaseMesh2D;

public class BitmapText extends Text {

	private BitmapFont font;

	public BitmapText(BitmapFont font, String text) {
		super(text, new Material(font.texture));
		this.font = font;
		buildMesh(font.texture, font.colums, font.rows);
	}

	private void buildMesh(Texture texture, int colums, int rows) {
		if (mesh != null)
			close();
		float tileWidth = (float) texture.getWidth() / (float) colums;
		float tileHeight = (float) texture.getHeight() / (float) rows;

		FloatList positions = new FloatArrayList();
		FloatList textCoords = new FloatArrayList();
		byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
		int numChars = chars.length;

		int currentWidth = 0;
		int currentHeight = 0;
		for (int i = 0; i < numChars; i++) {
			byte currChar = chars[i];
			if (currChar == '\n') {
				currentHeight += tileHeight;
				currentWidth = 0;
				continue;
			}

			int col = currChar % colums;
			int row = currChar / colums;

			positions.add((float) currentWidth * tileWidth);
			positions.add(-currentHeight);
			textCoords.add((float) col / (float) colums);
			textCoords.add((float) (row + 1) / (float) rows);

			positions.add((float) currentWidth * tileWidth);
			positions.add(tileHeight - currentHeight);
			textCoords.add((float) col / (float) colums);
			textCoords.add((float) row / (float) rows);

			positions.add((float) currentWidth * tileWidth + tileWidth);
			positions.add(-currentHeight);
			textCoords.add((float) (col + 1) / (float) colums);
			textCoords.add((float) (row + 1) / (float) rows);

			positions.add((float) currentWidth * tileWidth + tileWidth);
			positions.add(tileHeight - currentHeight);
			textCoords.add((float) (col + 1) / (float) colums);
			textCoords.add((float) row / (float) rows);
			currentWidth++;
		}
		mesh = new BaseMesh2D(positions.toFloatArray(), textCoords.toFloatArray());
		mesh.setMode(GL_TRIANGLE_STRIP);
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		buildMesh(font.texture, font.colums, font.rows);
	}

	public void setFont(Font font) {
		if (!(font instanceof BitmapFont))
			return;
		BitmapFont bitmapFont = (BitmapFont) font;
		if (this.font.colums != bitmapFont.colums || this.font.rows != bitmapFont.rows)
			buildMesh(bitmapFont.texture, bitmapFont.colums, bitmapFont.rows);
		this.material.setTexture(bitmapFont.texture);
		this.font = bitmapFont;
	}

	public BitmapFont getFont() {
		return font;
	}
}