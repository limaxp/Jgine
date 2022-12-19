package org.jgine.render.graphic.text;

import java.nio.IntBuffer;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh;

public abstract class Text implements AutoCloseable {

	public static final int TYPE_TRUETYPE = 0;
	public static final int TYPE_BITMAP = 1;

	protected Font font;
	protected int size;
	protected String text;
	protected Material material;
	protected BaseMesh mesh;

	public Text(Font font, int size, String text, Material material) {
		this.font = font;
		this.text = text;
		this.size = size;
		this.material = material;
	}

	@Override
	public void close() {
		mesh.close();
	}

	public abstract int getType();

	public void setFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void applyMaterial(Material material) {
		this.material.apply(material);
	}

	public Material getMaterial() {
		return material;
	}

	public BaseMesh getMesh() {
		return mesh;
	}

	public static int getCP(String text, int to, int i, IntBuffer cpOut) {
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
