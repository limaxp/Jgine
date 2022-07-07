package org.jgine.system.systems.tileMap;

import org.jgine.core.entity.Transform;
import org.jgine.misc.utils.loader.TileMapLoader.TileMapData;
import org.jgine.render.graphic.TileMap;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.system.SystemObject;

public class TileMapObject extends TileMap implements SystemObject {

	protected Transform transform;
	protected Material material;

	public TileMapObject(TileMapData data, Texture texture) {
		super(data, texture);
		material = new Material(texture);
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

	public Transform getTransform() {
		return transform;
	}

	public Material getMaterial() {
		return material;
	}
}
