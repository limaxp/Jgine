package org.jgine.render.graphic;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;

import org.jgine.misc.utils.BufferHelper;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapDataLayer;
import org.jgine.render.graphic.material.Material;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class TileMap implements AutoCloseable {

	protected int tilesheight;
	protected int tileswidth;
	protected int tileheight;
	protected int tilewidth;
	protected TileMapLayer[] layers;
	protected Material material;

	public TileMap(TileMapData data, Material material) {
		this.tilesheight = data.tilesheight;
		this.tileswidth = data.tileswidth;
		this.tileheight = data.tileheight;
		this.tilewidth = data.tilewidth;
		this.material = material;
		int layerSize = data.layers.length;
		layers = new TileMapLayer[layerSize];
		for (int i = 0; i < layerSize; i++) {
			TileMapDataLayer layerData = data.layers[i];
			layers[layerSize - 1 - layerData.number] = new TileMapLayer(layerData);
		}
	}

	@Override
	public final void close() {
		for (int i = 0; i < layers.length; i++)
			layers[i].close();
	}

	public final void render() {
		for (int i = 0; i < layers.length; i++)
			layers[i].render();
	}

	public Material getMaterial() {
		return material;
	}

	public class TileMapLayer implements AutoCloseable {

		public static final int VERTEX_SIZE = 2;
		public static final int TEXT_CORD_SIZE = 2;
		public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE;

		public static final int TILE_POS_SIZE = 2;
		public static final int TILE_TEXT_SIZE = 2;
		public static final int TILE_DATA_SIZE = 2; // rot, flipX
		public static final int TILE_SIZE = TILE_POS_SIZE + TILE_TEXT_SIZE + TILE_DATA_SIZE;

		public String name;
		protected int vao;
		protected int vbo;
		protected int databo;

		public TileMapLayer(TileMapDataLayer layerData) {
			name = layerData.name;
			vao = glGenVertexArrays();
			glBindVertexArray(vao);

			int widht = tilewidth / 2;
			int height = tileheight / 2;
			vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER,
					BufferHelper.createFloatBuffer(2, 2,
							new float[] { -widht, height, widht, height, -widht, -height, widht, -height },
							new float[] { 0, 0, 1, 0, 0, 1, 1, 1 }),
					GL_STATIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 2 * Float.BYTES);

			databo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			glBufferData(GL_ARRAY_BUFFER, buildFloatBuffer(layerData.tiles), GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, TILE_POS_SIZE, GL_FLOAT, false, TILE_SIZE * Float.BYTES, 0 * Float.BYTES);
			glVertexAttribDivisor(2, 1);
			glEnableVertexAttribArray(3);
			glVertexAttribPointer(3, TILE_TEXT_SIZE, GL_FLOAT, false, TILE_SIZE * Float.BYTES, 2 * Float.BYTES);
			glVertexAttribDivisor(3, 1);
			glEnableVertexAttribArray(4);
			glVertexAttribPointer(4, TILE_DATA_SIZE, GL_FLOAT, false, TILE_SIZE * Float.BYTES, 4 * Float.BYTES);
			glVertexAttribDivisor(4, 1);

			glBindVertexArray(0);
		}

		@Override
		public final void close() {
			glDeleteVertexArrays(vao);
			glDeleteBuffers(vbo);
			glDeleteBuffers(databo);
			vao = 0;
			vbo = 0;
			databo = 0;
		}

		public final void render() {
			glBindVertexArray(vao);
			glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, tileswidth * tilesheight);
			glBindVertexArray(0);
		}

		public final FloatBuffer buildFloatBuffer(TileMapTile[] tiles) {
			FloatBuffer buffer = BufferUtils.createFloatBuffer(tiles.length * TILE_SIZE);
			for (TileMapTile tile : tiles) {
				buffer.put(tile.x * tilewidth);
				buffer.put(tile.y * tileheight);
				int colums = material.getTexture().getColums();
				buffer.put(tile.tile % colums);
				buffer.put(tile.tile / colums);
				buffer.put(tile.rotation);
				buffer.put(tile.flipX == true ? 1.0f : 0.0f);
			}
			buffer.flip();
			return buffer;
		}

		public final void setTile(TileMapTile tile) {
			setTile(0, tile);
		}

		public final void setTile(int index, TileMapTile tile) {
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.mallocFloat(TILE_SIZE);
				buffer.put(tile.x * tilewidth);
				buffer.put(tile.y * tileheight);
				int colums = material.getTexture().getColums();
				buffer.put(tile.tile % colums);
				buffer.put(tile.tile / colums);
				buffer.put(tile.rotation);
				buffer.put(tile.flipX == true ? 1.0f : 0.0f);
				buffer.flip();
				glBufferSubData(GL_ARRAY_BUFFER, index * TILE_SIZE * Float.BYTES, buffer);
			}
		}

		public final TileMapTile getTile(int index) {
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.mallocFloat(TILE_SIZE);
				glGetBufferSubData(GL_ARRAY_BUFFER, index * TILE_SIZE * Float.BYTES, buffer);
				TileMapTile data = new TileMapTile();
				data.x = (int) buffer.get() / tilewidth;
				data.y = (int) buffer.get() / tileheight;
				int colum = (int) buffer.get();
				int row = (int) buffer.get();
				data.tile = row * tileswidth + colum;
				data.rotation = (int) buffer.get();
				data.flipX = buffer.get() == 1.0f ? true : false;
				return data;
			}
		}

		public Material getMaterial() {
			return material;
		}
	}

	public static class TileMapTile {

		public int tile;
		public int x;
		public int y;
		public int rotation;
		public boolean flipX;
	}
}
