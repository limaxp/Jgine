package org.jgine.system.systems.tileMap;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

public class TileMapSystem extends EngineSystem {

	public TileMapSystem() {
		super("tilemap");
	}
	
	@Override
	public TileMapScene createScene(Scene scene) {
		return new TileMapScene(this, scene);
	}

	@Override
	public TileMapObject load(Map<String, Object> data) {

		return null;
	}
}
