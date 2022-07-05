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
		return (x1 - x2) * (x1 - x2) < (a.w + b.w) * (a.w + b.w) && (y1 - y2) * (y1 - y2) < (a.h + b.h) * (a.h + b.h);
	}

	public static boolean checkBoundingCirclevsBoundingCircle(Vector2f pos1, BoundingCircle a, Vector2f pos2,
			BoundingCircle b) {
		return checkBoundingCirclevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkBoundingCirclevsBoundingCircle(float x1, float y1, BoundingCircle a, float x2, float y2,
			BoundingCircle b) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < (a.r + b.r) * (a.r + b.r);
	}

	public static boolean checkAxisAlignedBoundingQuadvsBoundingCircle(Vector2f pos1, AxisAlignedBoundingQuad a,
			Vector2f pos2, BoundingCircle b) {
		return checkAxisAlignedBoundingQuadvsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkAxisAlignedBoundingQuadvsBoundingCircle(float x1, float y1, AxisAlignedBoundingQuad a,
			float x2, float y2, BoundingCircle b) {
		return (x1 - x2) * (x1 - x2) < (a.w + b.r) * (a.w + b.r) && (y1 - y2) * (y1 - y2) < (a.h + b.r) * (a.h + b.r);
	}

	public static boolean checkLinevsLine(Vector2f pos1, LineCollider a, Vector2f pos2, LineCollider b) {
		return checkLinevsLine(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkLinevsLine(float x1, float y1, LineCollider a, float x2, float y2, LineCollider b) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean checkLinevsAxisAlignedBoundingQuad(Vector2f pos1, LineCollider a, Vector2f pos2,
			AxisAlignedBoundingQuad b) {
		return checkLinevsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkLinevsAxisAlignedBoundingQuad(float x1, float y1, LineCollider a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		return (x2 - x1) * a.normal.x < b.w && (y2 - y1) * a.normal.y < b.h;
	}

	public static boolean checkLinevsBoundingCircle(Vector2f pos1, LineCollider a, Vector2f pos2, BoundingCircle b) {
		return checkLinevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	public static boolean checkLinevsBoundingCircle(float x1, float y1, LineCollider a, float x2, float y2,
			BoundingCircle b) {
		return Vector2f.dot(x2 - x1, y2 - y1, a.normal.x, a.normal.y) < b.r;
	}

	@Nullable
	public static Collision resolveBoundingCirclevsBoundingCircle(Vector2f pos1, BoundingCircle a, Vector2f pos2,
			BoundingCircle b) {
		return resolveBoundingCirclevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveBoundingCirclevsBoundingCircle(float x1, float y1, BoundingCircle a, float x2,
			float y2, BoundingCircle b) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float dist = Vector2f.length(axisX, axisY);
		float minDist = a.r + b.r;
		if (dist < minDist) {
			float delta = minDist - dist;
			return new Collision(a, b, axisX, axisY, x1, y1, delta, delta);
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
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = a.w + b.w;
		float minDistY = a.h + b.h;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new Collision(a, b, axisX, axisY, x1, y1, deltaX, deltaY);
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
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = a.w + b.r;
		float minDistY = a.h + b.r;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new Collision(a, b, axisX, axisY, x1, y1, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveLinevsLine(Vector2f pos1, LineCollider a, Vector2f pos2, LineCollider b) {
		return resolveLinevsLine(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveLinevsLine(float x1, float y1, LineCollider a, float x2, float y2, LineCollider b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Nullable
	public static Collision resolveLinevsAxisAlignedBoundingQuad(Vector2f pos1, LineCollider a, Vector2f pos2,
			AxisAlignedBoundingQuad b) {
		return resolveLinevsAxisAlignedBoundingQuad(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveLinevsAxisAlignedBoundingQuad(float x1, float y1, LineCollider a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		// TODO seems to not work after being completely on wrong side
		float distX = (x2 - x1) * a.normal.x;
		float distY = (y2 - y1) * a.normal.y;
		if (distX < b.w && distY < b.h) {
//			System.out.println(distX + "    " + distY);
			float deltaX = b.w - distX;
			float deltaY = b.h - distY;
			return new Collision(a, b, -a.normal.x, -a.normal.y, x2, y2, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveLinevsBoundingCircle(Vector2f pos1, LineCollider a, Vector2f pos2,
			BoundingCircle b) {
		return resolveLinevsBoundingCircle(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolveLinevsBoundingCircle(float x1, float y1, LineCollider a, float x2, float y2,
			BoundingCircle b) {
		float dist = Vector2f.dot(x2 - x1, y2 - y1, a.normal.x, a.normal.y);
		if (dist < b.r) {
			float delta = b.r - dist;
			return new Collision(a, b, -a.normal.x, -a.normal.y, x2, y2, delta, delta);
		}
		return null;
	}
}
