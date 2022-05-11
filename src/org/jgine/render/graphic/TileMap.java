package org.jgine.render.graphic;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;
import java.util.List;

import org.jgine.misc.utils.BufferHelper;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData.TileMapLayer.TileMapTile;
import org.jgine.render.graphic.material.Texture;
import org.lwjgl.BufferUtils;

public class TileMap implements AutoCloseable {

	public static final int VERTEX_SIZE = 2;
	public static final int TEXT_CORD_SIZE = 2;
	public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE;

	public static final int TILE_POS_SIZE = 2;
	public static final int TILE_TEXT_SIZE = 2;
	public static final int TILE_DATA_SIZE = 2; // rot, flipX
	public static final int TILE_SIZE = TILE_POS_SIZE + TILE_TEXT_SIZE + TILE_DATA_SIZE;

	protected int vao;
	protected int vbo;
	protected int databo;
	protected TileMapData data;

	public TileMap(TileMapData data, Texture texture) {
		this.data = data;
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		int widht = data.tilewidth / 2;
		int height = data.tileheight / 2;
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, BufferHelper.createFloatBuffer(2, 2, new float[] { -widht, height, widht, height,
				-widht, -height, widht, -height },
				new float[] { 0, 0, 1, 0, 0, 1, 1, 1 }), GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 2 * Float.BYTES);

		databo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, databo);
		glBufferData(GL_ARRAY_BUFFER, buildFloatBuffer(data, texture), GL_STATIC_DRAW);
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
	}

	public final void render() {
		glBindVertexArray(vao);
		glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, data.tileswidth * data.tilesheight);
		glBindVertexArray(0);
	}

	public static FloatBuffer buildFloatBuffer(TileMapData data, Texture texture) {
		// TODO layer handling!
		List<TileMapTile> tiles = data.layers.get(0).tiles;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(tiles.size() * TILE_SIZE);
		// TODO sort with index!
		for (TileMapTile tile : tiles) {
			buffer.put((float) tile.x * data.tilewidth);
			buffer.put((float) tile.y * data.tileheight);

			buffer.put(tile.tile % texture.getColums());
			buffer.put(tile.tile / texture.getColums());

			buffer.put(tile.rotation);
			buffer.put(tile.flipX == true ? 1.0f : 0.0f);
		}
		buffer.flip();
		return buffer;
	}

	public TileMapData getData() {
		return data;
	}
}
