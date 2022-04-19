package org.jgine.system.systems.graphic2D;

import org.jgine.render.graphic.material.Material;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.transform.Transform;

public class Graphic2DObject implements SystemObject, Cloneable{

	protected Transform transform;
	protected Material material;

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public Graphic2DObject clone() {
		try {
			Graphic2DObject obj = (Graphic2DObject) super.clone();
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

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
}
