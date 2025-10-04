package org.jgine.render.mesh;

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
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;

import org.jgine.render.material.Material;
import org.jgine.system.systems.tileMap.TileMapData;
import org.jgine.system.systems.tileMap.TileMapData.TileMapLayer;
import org.jgine.system.systems.tileMap.TileMapData.TileMapTile;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class TileMapMesh implements AutoCloseable {

	protected int tilesheight;
	protected int tileswidth;
	protected int tileheight;
	protected int tilewidth;
	protected TileMapMeshLayer[] layers;
	protected Material material;

	public TileMapMesh(TileMapData data, Material material) {
		this.tilesheight = data.tilesheight;
		this.tileswidth = data.tileswidth;
		this.tileheight = data.tileheight;
		this.tilewidth = data.tilewidth;
		this.material = material;
		int layerSize = data.layers.length;
		layers = new TileMapMeshLayer[layerSize];
		for (int i = 0; i < layerSize; i++) {
			TileMapLayer layerData = data.layers[i];
			layers[layerSize - 1 - layerData.number] = new TileMapMeshLayer(layerData);
		}
	}

	@Override
	public final void close() {
		for (int i = 0; i < layers.length; i++)
			layers[i].close();
	}

	public boolean isClosed() {
		return layers[0].isClosed();
	}

	public TileMapMeshLayer getLayer(int index) {
		return layers[index];
	}

	public int getLayerSize() {
		return layers.length;
	}

	public final void setTile(int layer, int index, TileMapTile tile) {
		layers[layer].setTile(index, tile);
	}

	public final TileMapTile getTile(int layer, int index) {
		return layers[layer].getTile(index);
	}

	public int getTilewidth() {
		return tilewidth;
	}

	public int getTileheight() {
		return tileheight;
	}

	public int getTileswidth() {
		return tileswidth;
	}

	public int getTilesheight() {
		return tilesheight;
	}

	public Material getMaterial() {
		return material;
	}

	public class TileMapMeshLayer extends BaseMesh {

		public static final int DATA_SIZE = 6; // x,y,textX,textY,rot,flipX

		protected int databo;

		public TileMapMeshLayer(TileMapLayer layerData) {
			super(2, false);
			mode = GL_TRIANGLE_STRIP;
			int widht = tilewidth / 2;
			int height = tileheight / 2;
			loadVertices(new float[] { -widht, height, widht, height, -widht, -height, widht, -height },
					new float[] { 0, 0, 1, 0, 0, 1, 1, 1 });
			databo = glGenBuffers();

			glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			glBufferData(GL_ARRAY_BUFFER, buildFloatBuffer(layerData.tiles), GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 0 * Float.BYTES);
			glVertexAttribDivisor(2, 1);
			glEnableVertexAttribArray(3);
			glVertexAttribPointer(3, 2, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 2 * Float.BYTES);
			glVertexAttribDivisor(3, 1);
			glEnableVertexAttribArray(4);
			glVertexAttribPointer(4, 2, GL_FLOAT, false, DATA_SIZE * Float.BYTES, 4 * Float.BYTES);
			glVertexAttribDivisor(4, 1);
			glBindVertexArray(0);
		}

		@Override
		public final void close() {
			super.close();
			glDeleteBuffers(databo);
			databo = 0;
		}

		public final FloatBuffer buildFloatBuffer(TileMapTile[] tiles) {
			FloatBuffer buffer = BufferUtils.createFloatBuffer(tiles.length * DATA_SIZE);
			for (TileMapTile tile : tiles) {
				buffer.put(tile.x * tilewidth);
				buffer.put(tile.y * tileheight);
				buffer.put(tile.tile % tileswidth);
				buffer.put(tile.tile / tileswidth);
				buffer.put(tile.rotation);
				buffer.put(tile.flipX == true ? 1.0f : 0.0f);
			}
			buffer.flip();
			return buffer;
		}

		public final void setTile(int index, TileMapTile tile) {
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
				buffer.put(tile.x * tilewidth);
				buffer.put(tile.y * tileheight);
				buffer.put(tile.tile % tileswidth);
				buffer.put(tile.tile / tileswidth);
				buffer.put(tile.rotation);
				buffer.put(tile.flipX == true ? 1.0f : 0.0f);
				buffer.flip();
				glBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
			}
		}

		public final TileMapTile getTile(int index) {
			glBindBuffer(GL_ARRAY_BUFFER, databo);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.mallocFloat(DATA_SIZE);
				glGetBufferSubData(GL_ARRAY_BUFFER, index * DATA_SIZE * Float.BYTES, buffer);
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
}
