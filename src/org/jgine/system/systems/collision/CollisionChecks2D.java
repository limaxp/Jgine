package org.jgine.system.systems.collision;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.LineCollider;

public class CollisionChecks2D {

	public static boolean checkAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(Vector2f pos1,
			AxisAlignedBoundingQuad a, Vector2f pos2, AxisAlignedBoundingQuad b) {
		return checkAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(float x1, float y1,
			AxisAlignedBoundingQuad a, float x2, float y2, AxisAlignedBoundingQuad b) {
		if (((x1 - a.w / 2) <= (x2 + b.w / 2) && (x1 + a.w / 2) >= (x2 - b.w / 2))
				&& ((y1 - a.h / 2) <= (y2 + b.h / 2) && (y1 + a.h / 2) >= (y2 - b.h / 2)))
			return true;
		return false;
	}

	public static boolean checkBoundingCirclevsBoundingCircle(Vector2f pos1, BoundingCircle a, Vector2f pos2,
			BoundingCircle b) {
		return checkBoundingCirclevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkBoundingCirclevsBoundingCircle(float x1, float y1, BoundingCircle a, float x2, float y2,
			BoundingCircle b) {
		float distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		return distance < (a.r + b.r) * (a.r + b.r);
	}

	public static boolean checkAxisAlignedBoundingQuadvsBoundingCircle(Vector2f pos1, AxisAlignedBoundingQuad a,
			Vector2f pos2, BoundingCircle b) {
		return checkAxisAlignedBoundingQuadvsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkAxisAlignedBoundingQuadvsBoundingCircle(float x1, float y1, AxisAlignedBoundingQuad a,
			float x2, float y2, BoundingCircle b) {
		float x = Math.max((x1 - a.w / 2), Math.min(x2, (x1 + a.w / 2)));
		float y = Math.max((y1 - a.h / 2), Math.min(y2, (y1 + a.h / 2)));
		float distance = (x - x2) * (x - x2) + (y - y2) * (y - y2);
		return distance < b.r * b.r;
	}

	public static boolean checkPlanevsPlane(Vector2f pos1, LineCollider a, Vector2f pos2, LineCollider b) {
		return checkPlanevsPlane(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkPlanevsPlane(float x1, float y1, LineCollider a, float x2, float y2, LineCollider b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkPlanevsAxisAlignedBoundingQuad(Vector2f pos1, LineCollider a, Vector2f pos2,
			AxisAlignedBoundingQuad b) {
		return checkPlanevsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkPlanevsAxisAlignedBoundingQuad(float x1, float y1, LineCollider a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkPlanevsBoundingCircle(Vector2f pos1, LineCollider a, Vector2f pos2, BoundingCircle b) {
		return checkPlanevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkPlanevsBoundingCircle(float x1, float y1, LineCollider a, float x2, float y2,
			BoundingCircle b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Nullable
	public static Collision resolveBoundingCirclevsBoundingCircle(Vector2f pos1, BoundingCircle a, Vector2f pos2,
			BoundingCircle b) {
		return resolveBoundingCirclevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveBoundingCirclevsBoundingCircle(float x1, float y1, BoundingCircle a, float x2,
			float y2, BoundingCircle b) {
		Vector2f collisionAxis = Vector2f.sub(x1, y1, x2, y2);
		float dist = Vector2f.length(collisionAxis);
		float minDist = a.r + b.r;
		if (dist < minDist) {
			float delta = minDist - dist;
			return new Collision(collisionAxis, new Vector2f(x1, y1), new Vector2f(delta), a, b);
		}
		return null;
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(Vector2f pos1,
			AxisAlignedBoundingQuad a, Vector2f pos2, AxisAlignedBoundingQuad b) {
		return resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(float x1, float y1,
			AxisAlignedBoundingQuad a, float x2, float y2, AxisAlignedBoundingQuad b) {
		Vector2f collisionAxis = Vector2f.sub(x1, y1, x2, y2);
		float distX = Math.abs(collisionAxis.x);
		float minDistX = a.w + b.w;
		float distY = Math.abs(collisionAxis.y);
		float minDistY = a.h + b.h;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new Collision(collisionAxis, new Vector2f(x1, y1), new Vector2f(deltaX, deltaY), a, b);
		}
		return null;
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsBoundingCircle(Vector2f pos1, AxisAlignedBoundingQuad a,
			Vector2f pos2, BoundingCircle b) {
		return resolveAxisAlignedBoundingQuadvsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsBoundingCircle(float x1, float y1,
			AxisAlignedBoundingQuad a, float x2, float y2, BoundingCircle b) {
		Vector2f collisionAxis = Vector2f.sub(x1, y1, x2, y2);
		float distX = Math.abs(collisionAxis.x);
		float minDistX = a.w + b.r;
		float distY = Math.abs(collisionAxis.y);
		float minDistY = a.h + b.r;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new Collision(collisionAxis, new Vector2f(x1, y1), new Vector2f(deltaX, deltaY), a, b);
		}
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsPlane(Vector2f pos1, LineCollider a, Vector2f pos2, LineCollider b) {
		return resolvePlanevsPlane(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolvePlanevsPlane(float x1, float y1, LineCollider a, float x2, float y2,
			LineCollider b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsAxisAlignedBoundingQuad(Vector2f pos1, LineCollider a, Vector2f pos2,
			AxisAlignedBoundingQuad b) {
		return resolvePlanevsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolvePlanevsAxisAlignedBoundingQuad(float x1, float y1, LineCollider a, float x2,
			float y2, AxisAlignedBoundingQuad b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsBoundingCircle(Vector2f pos1, LineCollider a, Vector2f pos2,
			BoundingCircle b) {
		return resolvePlanevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolvePlanevsBoundingCircle(float x1, float y1, LineCollider a, float x2, float y2,
			BoundingCircle b) {
		// TODO Auto-generated method stub
		return null;
	}

}
