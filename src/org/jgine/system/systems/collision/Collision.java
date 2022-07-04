package org.jgine.system.systems.collision;

import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;

public class Collision {

	public final Vector3f collisionAxis;
	public final Vector3f collisionPosition;
	public final Vector2f overlap;
	public final Collider collider1;
	public final Collider collider2;

	public Collision(Vector2f collisionAxis, Vector2f collisionPosition, Vector2f overlap, Collider collider1,
			Collider collider2) {
		this(new Vector3f(collisionAxis), new Vector3f(collisionPosition), new Vector3f(overlap), collider1, collider2);
	}

	public Collision(Vector3f collisionAxis, Vector3f collisionPosition, Vector2f overlap, Collider collider1,
			Collider collider2) {
		this.collisionAxis = collisionAxis;
		this.collisionPosition = collisionPosition;
		this.overlap = overlap;
		this.collider1 = collider1;
		this.collider2 = collider2;
	}
}
