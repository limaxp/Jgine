package org.jgine.misc.utils.loader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jgine.misc.utils.loader.TileMapLoader.TileMapData.TileMapLayer;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData.TileMapLayer.TileMapTile;

import com.fasterxml.jackson.databind.JsonNode;

public class TileMapLoader {

	public static TileMapData load(File file) {
		return load(JSONLoader.load(file));
	}

	public static TileMapData load(InputStream is) {
		return load(JSONLoader.load(is));
	}

	private static TileMapData load(JsonNode node) {
		TileMapData data = new TileMapData();
		if (node.has("tileshigh"))
			data.tilesheight = node.get("tileshigh").intValue();
		if (node.has("tileswide"))
			data.tileswidth = node.get("tileswide").intValue();
		if (node.has("tileheight"))
			data.tileheight = node.get("tileheight").intValue();
		if (node.has("tilewidth"))
			data.tilewidth = node.get("tilewidth").intValue();

		if (!node.has("layers"))
			return data;
		JsonNode layers = node.get("layers");
		if (!layers.isArray())
			return data;
		TileMapLayer layerData = new TileMapLayer();
		data.layers.add(layerData);
		for (final JsonNode layer : layers) {
			if (!layer.has("tiles"))
				continue;
			JsonNode tiles = layer.get("tiles");
			if (!tiles.isArray())
				continue;
			for (final JsonNode tile : tiles) {
				TileMapTile tileData = new TileMapTile();
				layerData.tiles.add(tileData);
				if (tile.has("tile"))
					tileData.tile = tile.get("tile").intValue();
				if (tile.has("rot"))
					tileData.rotation = tile.get("rot").intValue();
				if (tile.has("y"))
					tileData.y = tile.get("y").intValue();
				if (tile.has("flipX"))
					tileData.flipX = tile.get("flipX").booleanValue();
				if (tile.has("index"))
					tileData.index = tile.get("index").intValue();
				if (tile.has("x"))
					tileData.x = tile.get("x").intValue();
			}
		}
		return data;
	}

	public static class TileMapData {

		public int tilesheight = 1;
		public int tileswidth = 1;
		public int tileheight = 1;
		public int tilewidth = 1;
		public final List<TileMapLayer> layers = new ArrayList<TileMapLayer>();

		public static class TileMapLayer {

			public final List<TileMapTile> tiles = new ArrayList<TileMapTile>();

			public static class TileMapTile {

				public int tile;
				public int rotation;
				public int x;
				public int y;
				public boolean flipX;
				public int index;
			}
		}
	}
}
