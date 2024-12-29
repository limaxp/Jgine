package org.jgine.system.systems.tileMap;

import java.util.BitSet;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.TileMapMesh;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.tileMap.TileMapData.TileMapTile;

public class TileMap implements SystemObject {

	private Transform transform;
	private TileMapData data;
	private Material material;
	private TileMapMesh mesh;
	private boolean rebuildMesh;
	private BitSet hitboxes;

	public TileMap(TileMapData data, Material material) {
		this.data = data;
		this.material = material;
		rebuildMesh = true;
		hitboxes = new BitSet(data.tileswidth * data.tilesheight);
	}

	@Override
	public TileMap clone() {
		try {
			TileMap obj = (TileMap) super.clone();
			obj.data = data.clone();
			obj.material = material.clone();
			obj.rebuildMesh = true;
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	void setTransform(Transform transform) {
		this.transform = transform;
	}

	public Transform getTransform() {
		return transform;
	}

	public Material getMaterial() {
		return material;
	}

	TileMapMesh getMesh() {
		if (rebuildMesh) {
			rebuildMesh = false;
			mesh = new TileMapMesh(data, material);
		}
		return mesh;
	}

	public int getTilewidth() {
		return data.tilewidth;
	}

	public int getTileheight() {
		return data.tileheight;
	}

	public int getTileswidth() {
		return data.tileswidth;
	}

	public int getTilesheight() {
		return data.tilesheight;
	}

	public int index(int x, int y) {
		return y * data.tileswidth + x;
	}

	public int x(int index) {
		return index % data.tileswidth;
	}

	public int y(int index) {
		return index / data.tileswidth;
	}

	public void setTile(int layer, int x, int y, TileMapTile tile) {
		setTile(layer, index(x, y), tile);
	}

	public void setTile(int layer, int index, TileMapTile tile) {
		data.layers[layer].tiles[index] = tile;
		rebuildMesh = true;
	}

	public void setTile(int layer, int x, int y, int tile) {
		setTile(layer, index(x, y), tile);
	}

	public void setTile(int layer, int index, int tile) {
		data.layers[layer].tiles[index].tile = tile;
		rebuildMesh = true;
	}

	public void setTile(int layer, int x, int y, int tile, int rotation, boolean flipX) {
		setTile(layer, index(x, y), tile, rotation, flipX);
	}

	public void setTile(int layer, int index, int tile, int rotation, boolean flipX) {
		TileMapTile t = data.layers[layer].tiles[index];
		t.tile = tile;
		t.rotation = rotation;
		t.flipX = flipX;
		rebuildMesh = true;
	}

	public int getTile(int layer, int x, int y) {
		return getTile(layer, index(x, y));
	}

	public int getTile(int layer, int index) {
		return data.layers[layer].tiles[index].tile;
	}

	public void setRotation(int layer, int x, int y, int rotation) {
		setRotation(layer, index(x, y), rotation);
	}

	public void setRotation(int layer, int index, int rotation) {
		data.layers[layer].tiles[index].rotation = rotation;
		rebuildMesh = true;
	}

	public int getRotation(int layer, int x, int y) {
		return getRotation(layer, index(x, y));
	}

	public int getRotation(int layer, int index) {
		return data.layers[layer].tiles[index].rotation;
	}

	public void setFlipX(int layer, int x, int y, boolean flipX) {
		setFlipX(layer, index(x, y), flipX);
	}

	public void setFlipX(int layer, int index, boolean flipX) {
		data.layers[layer].tiles[index].flipX = flipX;
		rebuildMesh = true;
	}

	public boolean getFlipX(int layer, int x, int y) {
		return getFlipX(layer, index(x, y));
	}

	public boolean getFlipX(int layer, int index) {
		return data.layers[layer].tiles[index].flipX;
	}

	public void setHitbox(int index, boolean hitbox) {
		setHitbox(x(index), y(index), hitbox);
	}

	public void setHitbox(int x, int y, boolean hitbox) {
		hitboxes.set(index(x, y), hitbox);
		if (hitbox)
			createCollider(x, y);
		else
			deleteCollider(x, y);
	}

	public boolean hasHitbox(int x, int y) {
		return hasHitbox(index(x, y));
	}

	public boolean hasHitbox(int index) {
		return hitboxes.get(index);
	}

	void createCollider(int x, int y) {
		Entity entity = transform.getEntity();
		Entity child = new Entity(entity.scene, x * data.tilewidth, -y * data.tileheight);
		child.setParent(entity);
		PhysicObject physic = new PhysicObject();
		physic.hasGravity = false;
		physic.stiffness = 0.0f;
		child.addSystem(Engine.PHYSIC_SYSTEM, physic);
		AxisAlignedBoundingQuad collider = new AxisAlignedBoundingQuad(data.tilewidth * 0.5f, data.tileheight * 0.5f);
		child.addSystem(Engine.COLLISION_SYSTEM, collider);
	}

	void deleteCollider(int x, int y) {
		Entity entity = transform.getEntity();
		for (Entity child : entity.getChilds()) {
			if (child.transform.getX() == data.tilewidth * 0.5f && child.transform.getY() == -y * data.tileheight) {
				child.delete();
				return;
			}
		}
	}
}
