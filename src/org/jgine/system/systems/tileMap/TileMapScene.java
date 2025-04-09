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
import org.jgine.system.data.ObjectSystemScene;

public class TileMapScene extends ObjectSystemScene<TileMapSystem, TileMap> {

	public TileMapScene(TileMapSystem system, Scene scene) {
		super(system, scene, TileMap.class, 10000);
	}

	@Override
	public void free() {
		forEach(TileMap::close);
	}

	@Override
	public void init(Entity entity, TileMap object) {
		object.setTransform(entity.transform);
		for (int x = 0; x < object.getTileswidth(); x++) {
			for (int y = 0; y < object.getTilesheight(); y++) {
				if (object.hasHitbox(x, y))
					object.createCollider(x, y);
			}
		}
	}

	@Override
	public void remove(int index) {
		get(index).close();
		super.remove(index);
	}

	@Override
	public void render(float dt) {
		TileMapShader shader = Renderer.TILE_MAP_SHADER;
		Renderer.setShader(shader);
		for (int i = 0; i < size(); i++) {
			TileMap object = get(i);
			shader.setTileMapData(object.getTileswidth(), object.getTilesheight());
			Renderer2D.render(object.getTransform().getMatrix(), object.getMesh(), object.getMaterial());
		}
	}

	@Override
	protected void saveData(TileMap object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected TileMap loadData(DataInput in) throws IOException {
		TileMap object = new TileMap();
		object.load(in);
		return object;
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return get(index).getTransform();
	}
}
