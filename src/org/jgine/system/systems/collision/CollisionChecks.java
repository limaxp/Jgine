package org.jgine.system.systems.collision;

import org.jgine.system.systems.collision.collider.AxisAlignedBoundingBox;
import org.jgine.system.systems.collision.collider.BoundingCylinder;
import org.jgine.system.systems.collision.collider.BoundingSphere;
import org.jgine.system.systems.collision.collider.PlaneCollider;
import org.jgine.utils.math.vector.Vector3f;

public class CollisionChecks {

	public static boolean checkAxisAlignedBoundingBoxvsAxisAlignedBoundingBox(Vector3f pos1, AxisAlignedBoundingBox a,
			Vector3f pos2, AxisAlignedBoundingBox b) {
		return checkAxisAlignedBoundingBoxvsAxisAlignedBoundingBox(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z,
				b);
	}

	public static boolean checkAxisAlignedBoundingBoxvsAxisAlignedBoundingBox(float x1, float y1, float z1,
			AxisAlignedBoundingBox a, float x2, float y2, float z2, AxisAlignedBoundingBox b) {
		if (((x1 - a.w / 2) <= (x2 + b.w / 2) && (x1 + a.w / 2) >= (x2 - b.w / 2))
				&& ((y1 - a.h / 2) <= (y2 + b.h / 2) && (y1 + a.h / 2) >= (y2 - b.h / 2))
				&& ((z1 - a.d / 2) <= (z2 + b.d / 2) && (z1 + a.d / 2) >= (z2 - b.d / 2)))
			return true;
		return false;
	}

	public static boolean checkBoundingSpherevsBoundingSphere(Vector3f pos1, BoundingSphere a, Vector3f pos2,
			BoundingSphere b) {
		return checkBoundingSpherevsBoundingSphere(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkBoundingSpherevsBoundingSphere(float x1, float y1, float z1, BoundingSphere a, float x2,
			float y2, float z2, BoundingSphere b) {
		double distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
		return distance < (a.r + b.r) * (a.r + b.r);
	}

	public static boolean checkAxisAlignedBoundingBoxvsBoundingSphere(Vector3f pos1, AxisAlignedBoundingBox a,
			Vector3f pos2, BoundingSphere b) {
		return checkAxisAlignedBoundingBoxvsBoundingSphere(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkAxisAlignedBoundingBoxvsBoundingSphere(float x1, float y1, float z1,
			AxisAlignedBoundingBox a, float x2, float y2, float z2, BoundingSphere b) {
		double x = Math.max((x2 - a.w / 2), Math.min(x1, (x2 + a.w / 2)));
		double y = Math.max((y2 - a.h / 2), Math.min(y1, (y2 + a.h / 2)));
		double z = Math.max((z2 - a.d / 2), Math.min(z1, (z2 + a.d / 2)));
		double distance = (x - x1) * (x - x1) + (y - y1) * (y - y1) + (z - z1) * (z - z1);
		return distance < b.r * b.r;
	}

	public static boolean checkPlanevsPlane(Vector3f pos1, PlaneCollider a, Vector3f pos2, PlaneCollider b) {
		return checkPlanevsPlane(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkPlanevsPlane(float x1, float y1, float z1, PlaneCollider a, float x2, float y2, float z2,
			PlaneCollider b) {
		// TODO Auto-generated method stub
		// a.transform.pos.distance(vec)
		return false;
	}

	public static boolean checkPlanevsAxisAlignedBoundingBox(Vector3f pos1, PlaneCollider a, Vector3f pos2,
			AxisAlignedBoundingBox b) {
		return checkPlanevsAxisAlignedBoundingBox(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkPlanevsAxisAlignedBoundingBox(float x1, float y1, float z1, PlaneCollider a, float x2,
			float y2, float z2, AxisAlignedBoundingBox b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkPlanevsBoundingSphere(Vector3f pos1, PlaneCollider a, Vector3f pos2, BoundingSphere b) {
		return checkPlanevsBoundingSphere(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkPlanevsBoundingSphere(float x1, float y1, float z1, PlaneCollider a, float x2, float y2,
			float z2, BoundingSphere b) {
		float dist = Vector3f.dot(a.normal.x, a.normal.y, a.normal.z, x2 - x1, y2 - y1, z2 - z1);
		return dist < b.r ? true : false;
	}

	public static boolean checkBoundingCylindervsBoundingCylinder(Vector3f pos1, BoundingCylinder a, Vector3f pos2,
			BoundingSphere b) {
		return checkBoundingCylindervsBoundingCylinder(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkBoundingCylindervsBoundingCylinder(float x1, float y1, float z1, BoundingCylinder a,
			float x2, float y2, float z2, BoundingSphere b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkBoundingCylindervsBoundingSphere(Vector3f pos1, BoundingCylinder a, Vector3f pos2,
			BoundingSphere b) {
		return checkBoundingCylindervsBoundingSphere(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkBoundingCylindervsBoundingSphere(float x1, float y1, float z1, BoundingCylinder a,
			float x2, float y2, float z2, BoundingSphere b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkBoundingCylindervsAxisAlignedBoundingBox(Vector3f pos1, BoundingCylinder a,
			Vector3f pos2, AxisAlignedBoundingBox b) {
		return checkBoundingCylindervsAxisAlignedBoundingBox(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkBoundingCylindervsAxisAlignedBoundingBox(float x1, float y1, float z1,
			BoundingCylinder a, float x2, float y2, float z2, AxisAlignedBoundingBox b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkBoundingCylindervsPlane(Vector3f pos1, BoundingCylinder a, Vector3f pos2,
			PlaneCollider b) {
		return checkBoundingCylindervsPlane(pos1.x, pos1.y, pos1.z, a, pos2.x, pos2.y, pos2.z, b);
	}

	public static boolean checkBoundingCylindervsPlane(float x1, float y1, float z1, BoundingCylinder a, float x2,
			float y2, float z2, PlaneCollider b) {
		// TODO Auto-generated method stub
		return false;
	}

}
