package org.jgine.system.systems.tileMap;

import org.jgine.render.material.Material;
import org.jgine.render.mesh.TileMap;
import org.jgine.system.SystemObject;
import org.jgine.utils.loader.TileMapLoader.TileMapData;

public class TileMapObject extends TileMap implements SystemObject {

	public TileMapObject(TileMapData data, Material material, int colums, int rows) {
		super(data, material, colums, rows);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public TileMapObject clone() {
		try {
			TileMapObject obj = (TileMapObject) super.clone();
			obj.material = material.clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
