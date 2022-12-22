package org.jgine.system.systems.tileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Texture;
import org.jgine.system.data.TransformListSystemScene;

public class TileMapScene extends TransformListSystemScene<TileMapSystem, TileMapObject> {

	public TileMapScene(TileMapSystem system, Scene scene) {
		super(system, scene, TileMapObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, TileMapObject object) {
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.TILE_MAP_SHADER);
		for (int i = 0; i < size; i++) {
			TileMapObject object = objects[i];
			Renderer2D.render(transforms[i].getMatrix(), object, object.material);
		}
	}

	@Override
	public TileMapObject load(DataInput in) throws IOException {
		return new TileMapObject(new TileMapData(), Texture.NONE);
	}

	@Override
	public void save(TileMapObject object, DataOutput out) throws IOException {
	}
}
