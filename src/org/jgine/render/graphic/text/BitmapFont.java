package org.jgine.render.graphic.text;

import org.jgine.core.manager.ResourceManager;
import org.jgine.render.graphic.material.Texture;

public class BitmapFont implements Font {

	public static final BitmapFont CONSOLAS = new BitmapFont("Consolas", ResourceManager.getTexture("Consolas"), 16,
			16);

	public final String name;
	public final Texture texture;
	public final int colums;
	public final int rows;

	public BitmapFont(String name, Texture texture, int colums, int rows) {
		this.name = name;
		this.texture = texture;
		this.colums = colums;
		this.rows = rows;
	}
}