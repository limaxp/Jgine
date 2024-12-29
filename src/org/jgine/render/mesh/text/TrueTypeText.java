package org.jgine.render.mesh.text;

import org.jgine.render.material.Material;
import org.jgine.render.mesh.MeshGenerator;

public class TrueTypeText extends Text {

	public static boolean IS_KERNING = true;

	public TrueTypeText(TrueTypeFont font, int size, String text) {
		this(font, size, text, 0.0f, 0.0f);
	}

	public TrueTypeText(TrueTypeFont font, int size, String text, float xOffset, float yOffset) {
		super(font, size, text, new Material(), xOffset, yOffset);
	}

	@Override
	protected void buildMesh() {
		if (mesh != null)
			close();
		TrueTypeFontGenerated generatedFont = TrueTypeFontGenerated.get((TrueTypeFont) font);
		material.setTexture(generatedFont.texture);
		mesh = MeshGenerator.text(generatedFont, size, text, xOffset, yOffset);
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
		rebuildMesh = true;
	}

	@Override
	public TrueTypeFont getFont() {
		return (TrueTypeFont) font;
	}
}
