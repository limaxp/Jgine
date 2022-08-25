package org.jgine.system.systems.collision;

import org.jgine.core.entity.Entity;

public class Collision {

	public final CollisionData data;
	public final Entity target;
	public final Collider collider;
	public final Collider targetColider;

	public Collision(CollisionData data, Entity target, Collider collider, Collider targetColider) {
		this.data = data;
		this.target = target;
		this.collider = collider;
		this.targetColider = targetColider;
	}
}
