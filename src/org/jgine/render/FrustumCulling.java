package org.jgine.render;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.Perspective;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.collider.PlaneCollider;
import org.jgine.system.systems.graphic.GraphicObject;
import org.jgine.system.systems.transform.Transform;

public class FrustumCulling {

	public final static int TOP = 0;
	public final static int BOTTOM = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	public final static int NEARP = 4;
	public final static int FARP = 5;
	public final static int SIZE = 6;

	private PlaneCollider[] planes;
	private float nw, nh;

	public FrustumCulling() {
		planes = new PlaneCollider[SIZE];
		for (int i = 0; i < SIZE; i++)
			planes[i] = new PlaneCollider(new Transform(), Vector3f.NULL);
	}

	public boolean contains(GraphicObject graphicObject) {
		// if (graphicObject.components.collider == null) {
		// if (containsPoint(graphicObject.transform.pos.p))
		// graphicObject.render(renderEngine.getRenderer());
		// }
		// else if (containsCollider(graphicObject.components.collider.get(0)))
		// graphicObject.render(renderEngine);

		if (containsPoint(graphicObject.getTransform().getPosition()))
			return true;
		return false;
	}

	public boolean containsCollider(Collider collider) {
		for (int i = 0; i < SIZE; i++) {
			if (!planes[i].checkCollision(collider))
				return true;
		}
		return false;

	}

	public boolean containsPoint(Vector3f point) {
		for (int i = 0; i < SIZE; i++) {
			if (!planes[i].containsPoint(point))
				return false;
		}
		return true;
	}

	public void applyPerspective(Perspective perspective) {
		float aspectRatio = perspective.getWidth() / perspective.getHeight();
		float tang = (float) Math.tan(Math.toRadians(perspective.getFov() * 0.5));
		nh = perspective.getZNear() * tang;
		nw = nh * aspectRatio;
	}

	public void applyCamera(Camera camera) {
		Perspective perspective = camera.getPerspective();
		Vector3f nc, fc, x, y, z;

		Vector3f p = camera.getTransform().getPosition();
		Vector3f l = camera.getForward();
		Vector3f u = camera.getUp();

		// compute the Z axis of camera
		// this axis points in the opposite direction from
		// the looking direction
		z = Vector3f.sub(p, l);
		z = Vector3f.normalize(z);
		x = Vector3f.cross(u, z); // X axis of camera with given "up" vector and Z axis
		x = Vector3f.normalize(x);
		y = Vector3f.cross(z, x); // the real "up" vector is the cross product of Z and X

		// compute the centers of the near and far planes
		nc = Vector3f.sub(p, Vector3f.mult(z, perspective.getZNear()));
		fc = Vector3f.sub(p, Vector3f.mult(z, perspective.getZFar()));

		setPlane(planes[NEARP], nc, Vector3f.mult(z, -1));
		setPlane(planes[FARP], fc, z);

		Vector3f aux, normal;

		aux = Vector3f.sub(Vector3f.add(nc, Vector3f.mult(y, nh)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(aux, x);
		setPlane(planes[TOP], Vector3f.add(nc, Vector3f.mult(y, nh)), normal);

		aux = Vector3f.sub(Vector3f.sub(nc, Vector3f.mult(y, nh)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(x, aux);
		setPlane(planes[BOTTOM], Vector3f.sub(nc, Vector3f.mult(y, nh)), normal);

		aux = Vector3f.sub(Vector3f.sub(nc, Vector3f.mult(x, nw)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(aux, y);
		setPlane(planes[LEFT], Vector3f.sub(nc, Vector3f.mult(x, nw)), normal);

		aux = Vector3f.sub(Vector3f.add(nc, Vector3f.mult(x, nw)), p);
		aux = Vector3f.normalize(aux);
		normal = Vector3f.cross(y, aux);
		setPlane(planes[RIGHT], Vector3f.add(nc, Vector3f.mult(x, nw)), normal);
	}

	// private float nw, nh, fw, fh;
	// private Vector3f ntl,ntr,nbl,nbr,ftl,ftr,fbl,fbr;
	//
	// // compute the 4 corners of the frustum on the near plane
	// ntl = Vector3f.add(nc, Vector3f.sub(Vector3f.mult(Y, nh), Vector3f.mult(X,
	// nw)));
	// ntr = Vector3f.add(nc, Vector3f.add(Vector3f.mult(Y, nh), Vector3f.mult(X,
	// nw)));
	// nbl = Vector3f.sub(nc, Vector3f.sub(Vector3f.mult(Y, nh), Vector3f.mult(X,
	// nw)));
	// nbr = Vector3f.sub(nc, Vector3f.add(Vector3f.mult(Y, nh), Vector3f.mult(X,
	// nw)));
	//
	// // compute the 4 corners of the frustum on the far plane
	// ftl = Vector3f.add(fc, Vector3f.sub(Vector3f.mult(Y, fh), Vector3f.mult(X,
	// fw)));
	// ftr = Vector3f.add(fc, Vector3f.add(Vector3f.mult(Y, fh), Vector3f.mult(X,
	// fw)));
	// fbl = Vector3f.sub(fc, Vector3f.sub(Vector3f.mult(Y, fh), Vector3f.mult(X,
	// fw)));
	// fbr = Vector3f.sub(fc, Vector3f.add(Vector3f.mult(Y, fh), Vector3f.mult(X,
	// fw)));
	//
	// // compute the six planes
	// // the function set3Points assumes that the points
	// // are given in counter clockwise order
	// planes[TOP].set3Points(ntr, ntl, ftl);
	// planes[BOTTOM].set3Points(nbl, nbr, fbr);
	// planes[LEFT].set3Points(ntl, nbl, fbl);
	// planes[RIGHT].set3Points(nbr, ntr, fbr);
	// planes[NEARP].set3Points(ntl, ntr, nbr);
	// planes[FARP].set3Points(ftr, ftl, fbl);

	private static void setPlane(PlaneCollider plane, Vector3f pos, Vector3f normal) {
		plane.getTransform().setPositionNoUpdate(pos);
		plane.normal = normal;
	}
}
