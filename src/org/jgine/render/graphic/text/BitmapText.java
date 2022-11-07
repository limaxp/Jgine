package org.jgine.render.graphic.text;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

import java.nio.charset.Charset;

import org.jgine.misc.collection.list.FloatList;
import org.jgine.misc.collection.list.arrayList.FloatArrayList;
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
		BaseMesh2D mesh = new BaseMesh2D(positions.toFloatArray(), textCoords.toFloatArray());
		mesh.setMode(GL_TRIANGLE_STRIP);
		return mesh;
	}

}