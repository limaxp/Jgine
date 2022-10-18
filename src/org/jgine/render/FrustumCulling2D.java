package org.jgine.render;

import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.camera.Camera;

public class FrustumCulling2D {

	public float x1, y1, x2, y2;

	public boolean containsPoint(Vector2f vec) {
		return vec.x >= x1 && vec.x <= x2 && vec.y >= y1 && vec.y <= y2;
	}

	public void applyCamera(Camera camera, float delta) {
		Vector3f position = camera.getTransform().getPosition();
		float width = camera.getWidth();
		float height = camera.getHeight();
		x1 = position.x - delta - width * 0.5f;
		x2 = position.x + delta + width * 0.5f;
		y1 = position.y - delta - height * 0.5f;
		y2 = position.y + delta + height * 0.5f;
	}
}
