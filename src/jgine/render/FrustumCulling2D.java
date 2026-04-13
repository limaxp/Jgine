package jgine.render;

import jgine.system.camera.Camera;
import jgine.utils.math.FastMath;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector3f;

public class FrustumCulling2D {

	public float x1, y1, x2, y2;

	public boolean containsPoint(Vector2f vec) {
		return vec.x >= x1 && vec.x <= x2 && vec.y >= y1 && vec.y <= y2;
	}

	public void applyCamera(Camera camera, float delta) {
		Vector3f position = camera.getTransform().getPosition();
		float width = camera.getWidth();
		float height = camera.getHeight();
		float max = FastMath.max(width, height);
		x1 = position.x - delta - max * 0.5f;
		x2 = position.x + delta + max * 0.5f;
		y1 = position.y - delta - max * 0.5f;
		y2 = position.y + delta + max * 0.5f;
	}
}
