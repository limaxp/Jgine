package org.jgine.render.graphic.text;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.ModelGenerator;

public class TrueTypeText extends Text {

	public static boolean IS_KERNING = true;

	public TrueTypeText(TrueTypeFont font, int size, String text) {
		this(font, size, text, 0.0f, 0.0f);
	}

	public TrueTypeText(TrueTypeFont font, int size, String text, float xOffset, float yOffset) {
		super(font, size, text, new Material(), xOffset, yOffset);
		buildMesh();
	}

	@Override
	protected void buildMesh() {
		if (mesh != null)
			close();
		TrueTypeFontGenerated generatedFont = TrueTypeFontGenerated.get((TrueTypeFont) font);
		material.setTexture(generatedFont.texture);
		mesh = ModelGenerator.text(generatedFont, size, text, xOffset, yOffset);
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
}
