package org.jgine.utils.loader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jgine.render.mesh.TileMapMesh;
import org.jgine.system.systems.tileMap.TileMapData;
import org.jgine.system.systems.tileMap.TileMapData.TileMapLayer;
import org.jgine.system.systems.tileMap.TileMapData.TileMapTile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Helper class for loading {@link TileMapMesh} files.
 */
public class TileMapLoader {

	public static void save(TileMapData data, DataOutput out) throws IOException {
		out.writeInt(data.tileswidth);
		out.writeInt(data.tilesheight);
		out.writeInt(data.tilewidth);
		out.writeInt(data.tileheight);

		int layerSize = data.layers.length;
		out.writeInt(layerSize);
		for (int i = 0; i < layerSize; i++)
			saveLayer(data.layers[i], out);
	}

	public static TileMapData load(DataInput in) throws IOException {
		TileMapData data = new TileMapData();
		data.tileswidth = in.readInt();
		data.tilesheight = in.readInt();
		data.tilewidth = in.readInt();
		data.tileheight = in.readInt();

		int layerSize = in.readInt();
		data.layers = new TileMapLayer[layerSize];
		for (int i = 0; i < layerSize; i++)
			data.layers[i] = loadLayer(in);
		return data;
	}

	public static void saveLayer(TileMapLayer data, DataOutput out) throws IOException {
		out.writeUTF(data.name);
		out.writeInt(data.number);

		int tileSize = data.tiles.length;
		out.writeInt(tileSize);
		for (int i = 0; i < tileSize; i++)
			saveTile(data.tiles[i], out);
	}

	public static TileMapLayer loadLayer(DataInput in) throws IOException {
		TileMapLayer data = new TileMapLayer();
		data.name = in.readUTF();
		data.number = in.readInt();

		int tileSize = in.readInt();
		data.tiles = new TileMapTile[tileSize];
		for (int i = 0; i < tileSize; i++)
			data.tiles[i] = loadTile(in);
		return data;
	}

	public static void saveTile(TileMapTile data, DataOutput out) throws IOException {
		out.writeInt(data.tile);
		out.writeInt(data.rotation);
		out.writeInt(data.x);
		out.writeInt(data.y);
		out.writeBoolean(data.flipX);
	}

	public static TileMapTile loadTile(DataInput in) throws IOException {
		TileMapTile data = new TileMapTile();
		data.tile = in.readInt();
		data.rotation = in.readInt();
		data.x = in.readInt();
		data.y = in.readInt();
		data.flipX = in.readBoolean();
		return data;
	}

	public static TileMapData load(String s) {
		return load(JSONLoader.load(s));
	}

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
		data.layers = new TileMapLayer[layerSize];
		for (int i = 0; i < layerSize; i++)
			data.layers[i] = loadLayer(layers.get(i));
		return data;
	}

	public static TileMapLayer loadLayer(JsonNode layer) {
		TileMapLayer layerData = new TileMapLayer();
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
}
