package jgine.system.tileMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.render.material.Material;
import jgine.render.mesh.TileMapMesh;
import jgine.system.SystemObject;
import jgine.system.collision.collider.AxisAlignedBoundingQuad;
import jgine.system.physic.PhysicObject;
import jgine.system.tileMap.TileMapData.TileMapTile;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.loader.TileMapLoader;

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
	public void load(Map<String, Object> data) {
		Object tileMapData = data.get("tileMap");
		if (tileMapData instanceof String)
			this.data = TileMapLoader.load((String) tileMapData);

		ObjectUtils.toMaterial(material, data.get("material"));
	}

	@Override
	public final void save(Map<String, Object> data) {
		data.put("tileMap", null); // TODO

		Map<String, Object> materialMap = new HashMap<String, Object>();
		material.save(materialMap);
		data.put("material", materialMap);
	}

	@Override
	public void load(DataInput in) throws IOException {
		data = TileMapLoader.load(in);
		init();
		material.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		TileMapLoader.save(data, out);
		material.save(out);
	}

	@Override
	public int system() {
		return Engine.TILEMAP;
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
			if (mesh != null)
				mesh.close();
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
		Entity entity = new Entity(transform.getEntity().scene);
		Transform transform = entity.add(Engine.TRANSFORM, new Transform());
		transform.setPosition(x * data.tilewidth, -y * data.tileheight, 0.0f);
		transform.setParent(this.transform);
		PhysicObject physic = entity.add(Engine.PHYSIC, new PhysicObject());
		physic.setGravity(false);
		physic.setMoveable(false);
		AxisAlignedBoundingQuad collider = new AxisAlignedBoundingQuad(data.tilewidth * 0.5f, data.tileheight * 0.5f);
		entity.add(Engine.COLLISION, collider);
	}

	void deleteCollider(int x, int y) {
		for (Transform child : transform.getChilds()) {
			if (child.getX() == data.tilewidth * 0.5f && child.getY() == -y * data.tileheight) {
				child.getEntity().delete();
				return;
			}
		}
	}
}
