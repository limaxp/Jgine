package org.jgine.system.systems.collision;

import org.jgine.misc.math.vector.Vector3f;

public class Collision {

	public final Vector3f collisionAxis;
	public final Vector3f collisionPosition;
	public final Collider collider1;
	public final Collider collider2;

	public Collision(Vector3f collisionAxis, Vector3f collisionPosition, Collider collider1, Collider collider2) {
		this.collisionAxis = collisionAxis;
		this.collisionPosition = collisionPosition;
		this.collider1 = collider1;
		this.collider2 = collider2;
	}
}
