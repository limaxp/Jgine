package org.jgine.system.systems.particle;

import org.jgine.core.Transform;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.particle.Particle;
import org.jgine.system.SystemObject;

public class ParticleObject extends Particle implements SystemObject {

	Transform transform;
	protected Material material;

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public ParticleObject clone() {
		try {
			ParticleObject obj = (ParticleObject) super.clone();
			obj.material = material.clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Material setMaterial(Material material) {
		return this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
}
