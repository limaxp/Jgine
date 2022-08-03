package org.jgine.system.systems.collision;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;

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
		return Vector2f.cross(a.normal.x, b.normal.x, a.normal.y, b.normal.y) != 0;
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
		// TODO check line resolve! dist as overlap also seems false!
		float dist = Vector2f.cross(a.normal.x, b.normal.x, a.normal.y, b.normal.y);
		if (dist != 0) {
			float axisX = a.normal.x - b.normal.x;
			float axisY = a.normal.y - b.normal.y;
			return new Collision(a, b, axisX, axisY, x2, y2, dist, dist);
		}
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
		float distX = (x2 - x1) * a.normal.x;
		float distY = (y2 - y1) * a.normal.y;
		if (distX < b.w && distY < b.h) {
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

	@Nullable
	public static boolean checkPolygonvsPolygon(Vector2f pos1, PolygonCollider a, Vector2f pos2, PolygonCollider b) {
		return checkPolygonvsPolygon(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static boolean checkPolygonvsPolygon(float x1, float y1, PolygonCollider a, float x2, float y2,
			PolygonCollider b) {
		int aLength = a.points.length;
		int bLength = b.points.length;

		for (int i = 0; i < aLength; i += 4) {
			float pointAX = a.points[i];
			float pointAY = a.points[i + 1];
			float pointBX = a.points[i + 2];
			float pointBY = a.points[i + 3];

			float axisX = -pointBX - pointAX;
			float axisY = pointBY - pointAY;
			float axisInvLength = FastMath.invsqrt(axisX * axisX + axisY * axisY);
			axisX *= axisInvLength;
			axisY *= axisInvLength;

			float p1min = Vector2f.dot(axisX, axisY, a.points[0], a.points[1]);
			float p1max = p1min;
			for (int j = 2; j < aLength; j += 2) {
				float dot = Vector2f.dot(axisX, axisY, a.points[j], a.points[j + 1]);
				p1min = Math.min(p1min, dot);
				p1max = Math.max(p1max, dot);
			}

			float p2min = Vector2f.dot(axisX, axisY, b.points[0], b.points[1]);
			float p2max = p2min;
			for (int j = 2; j < bLength; j += 2) {
				float dot = Vector2f.dot(axisX, axisY, b.points[j], b.points[j + 1]);
				p2min = Math.min(p2max, dot);
				p2max = Math.max(p2max, dot);
			}

			float vOffsetX = x1 - x2;
			float vOffsetY = y1 - y2;
			float offset = Vector2f.dot(axisX, axisY, vOffsetX, vOffsetY);
			p1min += offset;
			p1max += offset;

			if (p1min - p2max > 0 || p2min - p1max > 0)
				return false;
		}
		return true;
	}

	@Nullable
	public static Collision resolvePolygonvsPolygon(Vector2f pos1, PolygonCollider a, Vector2f pos2,
			PolygonCollider b) {
		return resolvePolygonvsPolygon(pos1.x, pos1.y, a, pos2.x, pos2.y, b);
	}

	@Nullable
	public static Collision resolvePolygonvsPolygon(float x1, float y1, PolygonCollider a, float x2, float y2,
			PolygonCollider b) {
		float collisionOverlap = Float.MAX_VALUE;
		float collisionAxisX = 0;
		float collisionAxisY = 0;
		PolygonCollider sa = a;
		PolygonCollider sb = b;
		for (int shape = 0; shape < 2; shape++) {
			if (shape == 1) {
				sa = b;
				sb = a;
			}

			int aLength = sa.points.length;
			int bLength = sb.points.length;
			for (int i = 0; i < aLength; i += 2) {
				int i2 = (i + 2) % aLength;
				float pointAX = sa.points[i];
				float pointAY = sa.points[i + 1];
				float pointBX = sa.points[i2];
				float pointBY = sa.points[i2 + 1];

				float axisX = -(pointBY - pointAY);
				float axisY = pointBX - pointAX;
				float axisInvLength = FastMath.invsqrt(axisX * axisX + axisY * axisY);
				axisX *= axisInvLength;
				axisY *= axisInvLength;

				float p1min = Float.MAX_VALUE;
				float p1max = -Float.MAX_VALUE;
				for (int j = 0; j < aLength; j += 2) {
					float dot = Vector2f.dot(axisX, axisY, sa.points[j], sa.points[j + 1]);
					p1min = Math.min(p1min, dot);
					p1max = Math.max(p1max, dot);
				}

				float p2min = Float.MAX_VALUE;
				float p2max = -Float.MAX_VALUE;
				for (int j = 0; j < bLength; j += 2) {
					float dot = Vector2f.dot(axisX, axisY, sb.points[j], sb.points[j + 1]);
					p2min = Math.min(p2min, dot);
					p2max = Math.max(p2max, dot);
				}

				float vOffsetX = x1 - x2;
				float vOffsetY = y1 - y2;
				float offset = Vector2f.dot(axisX, axisY, vOffsetX, vOffsetY);
				p1min += offset;
				p1max += offset;

				float overlap = FastMath.min(p1max, p2max) - FastMath.max(p1min, p2min);
				if (overlap < 0)
					return null;
				else {
					if (overlap < collisionOverlap) {
						collisionOverlap = overlap;
						collisionAxisX = axisX;
						collisionAxisY = axisY;
					}
				}
			}
		}
		return new Collision(sa, sb, collisionAxisX, collisionAxisY, 0, 0, collisionOverlap, collisionOverlap);
	}
}
