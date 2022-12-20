package org.jgine.render.graphic.text;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.MeshGenerator;

public class BitmapText extends Text {

	public BitmapText(BitmapFont font, int size, String text) {
		this(font, size, text, 0.0f, 0.0f);
	}

	public BitmapText(BitmapFont font, int size, String text, float xOffset, float yOffset) {
		super(font, size, text, new Material(font.texture), xOffset, yOffset);
		buildMesh();
	}

	@Override
	protected void buildMesh() {
		if (mesh != null)
			close();
		mesh = MeshGenerator.text((BitmapFont) font, size, text, xOffset, yOffset);
	}

	@Override
	public int getType() {
		return TYPE_BITMAP;
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
}