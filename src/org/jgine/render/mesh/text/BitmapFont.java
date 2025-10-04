package org.jgine.render.mesh.text;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Texture;
import org.jgine.utils.loader.ResourceManager;
import org.lwjgl.system.MemoryStack;

public class BitmapFont implements Font {

	public static final int MAX_SIZE = 1000;
	private static final Map<String, BitmapFont> NAME_MAP = new HashMap<String, BitmapFont>();
	private static final BitmapFont[] ID_MAP = new BitmapFont[MAX_SIZE];
	private static int increment;

	public static final BitmapFont CONSOLAS = new BitmapFont("Consolas", ResourceManager.getTexture("Consolas"), 16,
			16);

	public final int id;
	public final String name;
	public final Texture texture;
	public final int colums;
	public final int rows;

	public BitmapFont(String name, Texture texture, int colums, int rows) {
		this.id = increment++;
		this.name = name;
		this.texture = texture;
		this.colums = colums;
		this.rows = rows;
		NAME_MAP.put(name, this);
		ID_MAP[id] = this;
		
		texture.bind();
		texture.setFilter(Texture.LINEAR);
		texture.unbind();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public float getStringHeight(int fontHeight) {
		float scale = (float) fontHeight / 64;
		return getTileHeight() * scale;
	}
	
	@Override
	public float getStringWidth(String text, int fontHeight) {
		return getStringWidth(text, 0, text.length(), fontHeight);
	}
	
	@Override
	public float getStringWidth(String text, int from, int to, int fontHeight) {
		try (MemoryStack stack = stackPush()) {
			float scale = (float) fontHeight / 64;
			float width = 0;
			IntBuffer pCodePoint = stack.mallocInt(1);
			int i = from;
			while (i < to) {
				i += BitmapText.getCP(text, to, i, pCodePoint);
				width++;
			}
			return width * getTileWidth() * scale;
		}
	}

	public float getTileWidth() {
		return (float) texture.getWidth() / (float) colums;
	}

	public float getTileHeight() {
		return (float) texture.getHeight() / (float) rows;
	}

	@Nullable
	public static BitmapFont get(String name) {
		return NAME_MAP.get(name);
	}

	@Nullable
	public static BitmapFont get(int id) {
		return ID_MAP[id];
	}
}