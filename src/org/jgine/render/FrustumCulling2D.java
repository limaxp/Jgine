package org.jgine.render;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;

public class FrustumCulling2D {

	private AxisAlignedBoundingQuad collider;

	public boolean containsCollider(Collider collider) {
//		return this.collider.checkCollision(collider);
		return true;
	}

	public boolean containsPoint(Vector3f vec) {
//		return collider.containsPoint(vec);
		return true;
	}

	public void applyCamera(Camera camera) {
		// TODO implement this
		// Vector position = camera.getTransform().getPosition();
		// float delta = 35;
		// x1 = (position.x - 1) - delta;
		// x2 = (position.x + 1) + delta;
		// y1 = (position.y - 1) - delta;
		// y2 = (position.y + 1) + delta;
//		collider = new AxisAlignedBoundingQuad(camera.getTransform(), 60, 20);
	}
}
