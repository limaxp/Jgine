package org.jgine.system.systems.collision;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.LineCollider;

public class CollisionChecks2D {

	public static boolean AxisAlignedBoundingBoxvsAxisAlignedBoundingBox(AxisAlignedBoundingQuad a,
			AxisAlignedBoundingQuad b) {
		Vector3f center = a.transform.getPosition();
		Vector3f otherCenter = b.transform.getPosition();
		if (((center.x - a.w / 2) <= (otherCenter.x + b.w / 2)
				&& (center.x + a.w / 2) >= (otherCenter.x - b.w / 2))
				&& ((center.y - a.h / 2) <= (otherCenter.y + b.h / 2)
						&& (center.y + a.h / 2) >= (otherCenter.y - b.h / 2)))
			return true;
		return false;
	}

	public static boolean BoundingCirclevsBoundingCircle(BoundingCircle a, BoundingCircle b) {
		Vector3f center = a.transform.getPosition();
		Vector3f otherCenter = b.transform.getPosition();
		float distance = (center.x - otherCenter.x) * (center.x -
				otherCenter.x)
				+ (center.y - otherCenter.y) * (center.y - otherCenter.y);
		return distance < (a.r + b.r) * (a.r + b.r);
	}

	public static boolean AxisAlignedBoundingBoxvsBoundingCircle(
			AxisAlignedBoundingQuad a, BoundingCircle b) {
		Vector3f center = a.transform.getPosition();
		Vector3f otherCenter = b.transform.getPosition();
		float x = Math.max((center.x - a.w / 2), Math.min(otherCenter.x,
				(center.x + a.w / 2)));
		float y = Math.max((center.y - a.h / 2), Math.min(otherCenter.y,
				(center.y + a.h / 2)));
		float distance = (x - otherCenter.x) * (x - otherCenter.x)
				+ (y - otherCenter.y) * (y - otherCenter.y);
		return distance < b.r * b.r;
	}

	public static boolean PlanevsPlane(LineCollider plane2d, LineCollider other) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean PlanevsAxisAlignedBoundingBox(LineCollider other,
			AxisAlignedBoundingQuad axisAlignedBoundingBox2D) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean PlanevsBoundingCircle(LineCollider other, BoundingCircle boundingCircle) {
		// TODO Auto-generated method stub
		return false;
	}

}
