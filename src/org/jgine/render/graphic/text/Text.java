package org.jgine.render.graphic.text;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.mesh.BaseMesh2D;

public abstract class Text implements AutoCloseable {

	protected String text;
	protected Material material;
	protected BaseMesh2D mesh;

	public Text(String text, Material material) {
		this.text = text;
		this.material = material;
	}

	@Override
	public void close() {
		material.getTexture().close();
		mesh.close();
	}

	public abstract void setFont(Font font);

	public abstract Font getFont();

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
