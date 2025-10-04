package org.jgine.system.systems.tileMap;

public class TileMapData implements Cloneable {

	public static TileMapData build(int tileswidth, int tilesheight, int tilewidth, int tileheight, int layers) {
		TileMapData object = new TileMapData();
		object.tileswidth = tileswidth;
		object.tilesheight = tilesheight;
		object.tilewidth = tilewidth;
		object.tileheight = tileheight;
		object.layers = new TileMapLayer[layers];
		int tileSize = tileswidth * tilesheight;

		for (int i = 0; i < layers; i++) {
			TileMapLayer layer = new TileMapLayer();
			object.layers[i] = layer;
			layer.number = i;
			layer.tiles = new TileMapTile[tileSize];
			for (int j = 0; j < tileSize; j++)
				layer.tiles[j] = new TileMapTile();
		}
		return object;
	}

	public int tileswidth = 1;
	public int tilesheight = 1;
	public int tilewidth = 1;
	public int tileheight = 1;
	public TileMapLayer[] layers;

	@Override
	public TileMapData clone() {
		try {
			TileMapData obj = (TileMapData) super.clone();
			obj.layers = new TileMapLayer[layers.length];
			for (int i = 0; i < layers.length; i++)
				obj.layers[i] = layers[i].clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class TileMapLayer implements Cloneable {

		public String name = "";
		public int number = 0;
		public TileMapTile[] tiles;

		@Override
		public TileMapLayer clone() {
			try {
				TileMapLayer obj = (TileMapLayer) super.clone();
				obj.tiles = new TileMapTile[tiles.length];
				for (int i = 0; i < tiles.length; i++)
					obj.tiles[i] = tiles[i].clone();
				return obj;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class TileMapTile implements Cloneable {

		public int tile;
		public int x;
		public int y;
		public int rotation;
		public boolean flipX;

		@Override
		public TileMapTile clone() {
			try {
				TileMapTile obj = (TileMapTile) super.clone();
				return obj;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
