package org.jgine.render;

import org.jgine.core.Transform;
import org.jgine.render.material.Material;
import org.jgine.system.systems.camera.Camera;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;

public class FrustumCulling {

	public static final double ANG2RAD = 3.14159265358979323846 / 180.0;

	private FrustumPlane top;
	private FrustumPlane bottom;
	private FrustumPlane left;
	private FrustumPlane right;
	private FrustumPlane near;
	private FrustumPlane far;

	public boolean containsPoint(Vector3f point) {
		if (!top.containsPoint(point))
			return false;
		if (!bottom.containsPoint(point))
			return false;
		if (!left.containsPoint(point))
			return false;
		if (!right.containsPoint(point))
			return false;
		if (!near.containsPoint(point))
			return false;
		if (!far.containsPoint(point))
			return false;
		return true;
	}

	public void applyCamera(Camera camera, float delta) {
		Vector3f p = camera.getTransform().getPosition();
		Vector3f l = camera.getForwardDirection();
		Vector3f u = camera.getUpDirection();

		// store the information
		float ratio = camera.getAspectRatio();
		float angle = camera.getFov();
		float nearD = camera.getZNear();
		float farD = camera.getZFar();

		// compute width and height of the near and far plane sections
		float tang = (float) Math.tan(ANG2RAD * angle * 0.5);
		float nh = nearD * tang;
		float nw = nh * ratio;
		float fh = farD * tang;
		float fw = fh * ratio;

		// compute the Z axis of camera
		// this axis points in the opposite direction from
		// the looking direction
		Vector3f z = Vector3f.sub(p, l);
		z = Vector3f.normalize(z);
		// X axis of camera with given "up" vector and Z axis
		Vector3f x = Vector3f.cross(u, z);
		x = Vector3f.normalize(x);
		// the real "up" vector is the cross product of Z and X
		Vector3f y = Vector3f.cross(z, x);

		// compute the centers of the near and far planes
		Vector3f nc = Vector3f.sub(p, Vector3f.mult(z, camera.getZNear()));
		Vector3f fc = Vector3f.sub(p, Vector3f.mult(z, camera.getZFar()));

		// TODO fix this

		near = new FrustumPlane(Vector3f.mult(z, -1), nc);
		far = new FrustumPlane(z, fc);

		Vector3f aux, normal;

		aux = Vector3f.sub(Vector3f.add(nc, Vector3f.mult(y, nh)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(aux, x);
		top = new FrustumPlane(normal, Vector3f.add(nc, Vector3f.mult(y, nh)));

		aux = Vector3f.sub(Vector3f.sub(nc, Vector3f.mult(y, nh)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(x, aux);
		bottom = new FrustumPlane(normal, Vector3f.sub(nc, Vector3f.mult(y, nh)));

		aux = Vector3f.sub(Vector3f.sub(nc, Vector3f.mult(x, nw)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(aux, y);
		left = new FrustumPlane(normal, Vector3f.sub(nc, Vector3f.mult(x, nw)));

		aux = Vector3f.sub(Vector3f.add(nc, Vector3f.mult(x, nw)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(y, aux);
		right = new FrustumPlane(normal, Vector3f.add(nc, Vector3f.mult(x, nw)));
	}

	public void render() {
		top.render();
		bottom.render();
		left.render();
		right.render();
		near.render();
		far.render();
	}

	public static class FrustumPlane {

		private Vector3f normal;
		private Vector3f pos;

		public FrustumPlane(Vector3f normal, Vector3f pos) {
			this.normal = normal;
			this.pos = pos;
		}

		public boolean containsPoint(Vector3f point) {
			Vector3f sub = Vector3f.sub(point, pos);
			return Vector3f.dot(normal, sub) > 0;
		}

		public void render() {
			Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), pos, normal, new Vector3f(Float.MAX_VALUE)),
					UIRenderer.BASIC_SHADER, new Material());
		}
	}
}
