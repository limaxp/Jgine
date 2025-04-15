package org.jgine.system.systems.tileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.TileMapMesh;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.tileMap.TileMapData.TileMapTile;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.loader.TileMapLoader;

public class TileMap implements SystemObject {

	private Transform transform;
	private TileMapData data;
	private Material material;
	private TileMapMesh mesh;
	private boolean rebuildMesh = true;
	private BitSet hitboxes;

	public TileMap(TileMapData data, Material material) {
		this.data = data;
		this.material = material;
		init();
	}

	public TileMap() {
		this.material = new Material();
	}

	private void init() {
		hitboxes = new BitSet(data.tileswidth * data.tilesheight);
	}

	public void close() {
		mesh.close();
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

	@SuppressWarnings("unchecked")
	public void load(Map<String, Object> data) {
		Object tileMapData = data.get("tileMap");
		if (tileMapData instanceof String)
			TileMapLoader.load((String) tileMapData);

		Object materialData = data.get("material");
		if (materialData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) materialData);
			if (texture != null)
				material.setTexture(texture);
		} else if (materialData instanceof Map)
			material.load((Map<String, Object>) materialData);
	}

	public void load(DataInput in) throws IOException {
		data = TileMapLoader.load(in);
		init();
		material.load(in);
	}

	public void save(DataOutput out) throws IOException {
		TileMapLoader.save(data, out);
		material.save(out);
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
		physic.setGravity(false);
		physic.setMoveable(false);
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
