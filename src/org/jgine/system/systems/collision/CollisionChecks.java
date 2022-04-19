package org.jgine.system.systems.collision;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingBox;
import org.jgine.system.systems.collision.collider.BoundingCylinder;
import org.jgine.system.systems.collision.collider.BoundingSphere;
import org.jgine.system.systems.collision.collider.PlaneCollider;

public class CollisionChecks {

	public static boolean AxisAlignedBoundingBoxvsAxisAlignedBoundingBox(AxisAlignedBoundingBox a,
			AxisAlignedBoundingBox b) {
		Vector3f center = a.transform.getPosition();
		Vector3f otherCenter = b.transform.getPosition();
		if (((center.x - a.w / 2) <= (otherCenter.x + b.w / 2)
				&& (center.x + a.w / 2) >= (otherCenter.x - b.w / 2))
				&& ((center.y - a.h / 2) <= (otherCenter.y + b.h / 2)
						&& (center.y + a.h / 2) >= (otherCenter.y - b.h / 2))
				&& ((center.z - a.d / 2) <= (otherCenter.z + b.d / 2)
						&& (center.z + a.d / 2) >= (otherCenter.z - b.d / 2)))
			return true;
		return false;
	}

	public static boolean BoundingSpherevsBoundingSphere(BoundingSphere a, BoundingSphere b) {
		Vector3f center = a.transform.getPosition();
		Vector3f otherCenter = b.transform.getPosition();
		double distance = (center.x - otherCenter.x) * (center.x - otherCenter.x)
				+ (center.y - otherCenter.y) * (center.y - otherCenter.y)
				+ (center.z - otherCenter.z) * (center.z - otherCenter.z);
		return distance < (a.r + b.r) * (a.r + b.r);
	}

	public static boolean AxisAlignedBoundingBoxvsBoundingSphere(AxisAlignedBoundingBox a,
			BoundingSphere b) {
		Vector3f center = b.transform.getPosition();
		Vector3f otherCenter = a.transform.getPosition();
		double x = Math.max((otherCenter.x - a.w / 2), Math.min(center.x, (otherCenter.x + a.w / 2)));
		double y = Math.max((otherCenter.y - a.h / 2), Math.min(center.y, (otherCenter.y + a.h / 2)));
		double z = Math.max((otherCenter.z - a.d / 2), Math.min(center.z, (otherCenter.z + a.d / 2)));
		double distance = (x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) + (z
				- center.z) * (z - center.z);
		return distance < b.r * b.r;
	}

	public static boolean PlanevsPlane(PlaneCollider a, PlaneCollider b) {
		// TODO Auto-generated method stub
		// a.transform.pos.distance(vec)
		return false;
	}

	public static boolean PlanevsAxisAlignedBoundingBox(PlaneCollider a, AxisAlignedBoundingBox b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean PlanevsBoundingSphere(PlaneCollider a, BoundingSphere b) {
		// TODO this is wrong!
		double distance = Vector3f.distance(a.transform.getPosition(), b.transform.getPosition());
		if (distance < -b.r)
			return false;
		else if (distance <= b.r)
			return true;
		return false;
	}

	public static boolean BoundingCylindervsBoundingCylinder(BoundingCylinder a, BoundingSphere b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean BoundingCylindervsBoundingSphere(BoundingCylinder a, BoundingSphere b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean BoundingCylindervsAxisAlignedBoundingBox(BoundingCylinder a, AxisAlignedBoundingBox b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean BoundingCylindervsPlane(BoundingCylinder a, PlaneCollider b) {
		// TODO Auto-generated method stub
		return false;
	}

}
