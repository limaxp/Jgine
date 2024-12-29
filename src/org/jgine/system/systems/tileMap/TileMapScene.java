package org.jgine.system.systems.tileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.shader.TileMapShader;
import org.jgine.system.data.ListSystemScene;

public class TileMapScene extends ListSystemScene<TileMapSystem, TileMap> {

	public TileMapScene(TileMapSystem system, Scene scene) {
		super(system, scene, TileMap.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, TileMap object) {
		object.setTransform(entity.transform);
		for (int x = 0; x < object.getTileswidth(); x++) {
			for (int y = 0; y < object.getTilesheight(); y++) {
				if (object.hasHitbox(x, y))
					object.createCollider(x, y);
			}
		}
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(float dt) {
		TileMapShader shader = Renderer.TILE_MAP_SHADER;
		for (int i = 0; i < size; i++) {
			TileMap object = objects[i];
			shader.setTileMapData(object.getTileswidth(), object.getTilesheight());
			Renderer2D.render(object.getTransform().getMatrix(), object.getMesh(), shader, object.getMaterial());
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return objects[index].getTransform();
	}
}
