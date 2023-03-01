package org.jgine.system.systems.tileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.TileMap.TileMapLayer;
import org.jgine.render.mesh.TileMap.TileMapTile;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.utils.loader.TileMapLoader.TileMapData;

public class TileMapScene extends TransformListSystemScene<TileMapSystem, TileMapObject> {

	public TileMapScene(TileMapSystem system, Scene scene) {
		super(system, scene, TileMapObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, TileMapObject object) {
		// TODO implement this!
//		for (int i = 0; i < object.getLayerSize(); i++) {
		for (int i = 0; i < 1; i++) {
			TileMapLayer layer = object.getLayer(i);

			for (int x = 0; x < object.getTileswidth(); x++) {
				for (int y = 0; y < object.getTilesheight(); y++) {
					TileMapTile tile = layer.getTile(x * object.getTileswidth() + y);
					if (x == 0 || y == 0 || x == object.getTileswidth() - 1 || y == object.getTilesheight() - 1)
						createCollider(entity, x, y, object.getTilewidth(), object.getTileheight());
				}
			}
		}
	}

	private void createCollider(Entity parent, int x, int y, int width, int height) {
		Entity entity = new Entity(scene, x * width, -y * height);
		entity.setParent(parent);
		PhysicObject physic = new PhysicObject(); // TODO remove physic later
		physic.hasGravity = false;
		physic.stiffness = 0.0f;
		entity.addSystem(Engine.PHYSIC_SYSTEM, physic);
		AxisAlignedBoundingQuad collider = new AxisAlignedBoundingQuad(width * 0.5f, height * 0.5f);
		entity.addSystem(Engine.COLLISION_SYSTEM, collider);
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.TILE_MAP_SHADER);
		for (int i = 0; i < size; i++) {
			TileMapObject object = objects[i];
			Renderer2D.render(transforms[i].getMatrix(), object, object.getMaterial());
		}
	}

	@Override
	public TileMapObject load(DataInput in) throws IOException {
		return new TileMapObject(new TileMapData(), new Material(Texture.NONE));
	}

	@Override
	public void save(TileMapObject object, DataOutput out) throws IOException {
	}
}
