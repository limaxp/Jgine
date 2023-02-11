package org.jgine.system.systems.particle;

import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.particle.BillboardParticle;
import org.jgine.system.SystemObject;

public class ParticleObject extends BillboardParticle implements SystemObject {

	protected Material material;
	protected boolean isActive = true;

	public void update() {
	}

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

	public boolean isActive() {
		return isActive;
	}

	public Material setMaterial(Material material) {
		return this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
}
