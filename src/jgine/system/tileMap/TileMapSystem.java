package jgine.system.tileMap;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;

public class TileMapSystem extends EngineSystem<TileMapSystem, TileMap> {

	public TileMapSystem() {
		super("tilemap");
	}

	@Override
	public TileMapScene createScene(Scene scene) {
		return new TileMapScene(this, scene);
	}

	@Override
	public TileMap load(Map<String, Object> data) {
		TileMap object = new TileMap();
		object.load(data);
		return object;
	}
}
