package org.jgine.render.graphic.text;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh2D;

public abstract class Text implements AutoCloseable {

	public static final int TYPE_TRUETYPE = 0;
	public static final int TYPE_BITMAP = 1;

	protected Font font;
	protected int size;
	protected String text;
	protected Material material;
	protected BaseMesh2D mesh;

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

	public BaseMesh2D getMesh() {
		return mesh;
	}
}
