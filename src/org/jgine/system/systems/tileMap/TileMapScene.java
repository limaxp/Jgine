package org.jgine.system.systems.tileMap;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class TileMapScene extends EntityListSystemScene<TileMapSystem, TileMapObject> {

	public TileMapScene(TileMapSystem system, Scene scene) {
		super(system, scene, TileMapObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, TileMapObject object) {
		object.transform = entity.transform;
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.TILE_MAP_SHADER);
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer2D.setCamera(camera);
		for (int i = 0; i < size; i++) {
			TileMapObject object = objects[i];
			Renderer2D.render(object.transform.getMatrix(), object, object.material);
		}
	}
}
