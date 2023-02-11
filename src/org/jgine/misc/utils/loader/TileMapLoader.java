package org.jgine.misc.utils.loader;

import java.io.File;
import java.io.InputStream;

import org.jgine.render.mesh.TileMap.TileMapTile;

import com.fasterxml.jackson.databind.JsonNode;

public class TileMapLoader {

	public static TileMapData load(File file) {
		return load(JSONLoader.load(file));
	}

	public static TileMapData load(InputStream is) {
		return load(JSONLoader.load(is));
	}

	public static TileMapData load(JsonNode node) {
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
		int layerSize = layers.size();
		data.layers = new TileMapDataLayer[layerSize];
		for (int i = 0; i < layerSize; i++)
			data.layers[i] = loadLayer(layers.get(i));
		return data;
	}

	public static TileMapDataLayer loadLayer(JsonNode layer) {
		TileMapDataLayer layerData = new TileMapDataLayer();
		if (layer.has("name"))
			layerData.name = layer.get("name").toString();
		if (layer.has("number"))
			layerData.number = layer.get("number").intValue();

		if (!layer.has("tiles"))
			return layerData;
		JsonNode tiles = layer.get("tiles");
		if (!tiles.isArray())
			return layerData;
		int tileSize = tiles.size();
		layerData.tiles = new TileMapTile[tileSize];
		for (int j = 0; j < tileSize; j++) {
			JsonNode tile = tiles.get(j);
			int index = j;
			if (tile.has("index"))
				index = tile.get("index").intValue();
			layerData.tiles[index] = loadTile(tile);
		}
		return layerData;
	}

	public static TileMapTile loadTile(JsonNode tile) {
		TileMapTile tileData = new TileMapTile();
		if (tile.has("tile"))
			tileData.tile = tile.get("tile").intValue();
		if (tile.has("rot"))
			tileData.rotation = tile.get("rot").intValue();
		if (tile.has("y"))
			tileData.y = tile.get("y").intValue();
		if (tile.has("flipX"))
			tileData.flipX = tile.get("flipX").booleanValue();
		if (tile.has("x"))
			tileData.x = tile.get("x").intValue();
		return tileData;
	}

	public static class TileMapData {

		public int tilesheight = 1;
		public int tileswidth = 1;
		public int tileheight = 1;
		public int tilewidth = 1;
		public TileMapDataLayer[] layers;
	}

	public static class TileMapDataLayer {

		public String name = "";
		public int number = 0;
		public TileMapTile[] tiles;
	}
}
