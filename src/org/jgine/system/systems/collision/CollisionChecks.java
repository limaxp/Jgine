package org.jgine.system.systems.collision;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.CircleCollider;
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Helper class for collision checks. Used by {@link Collider} classes.
 */
public class CollisionChecks {

	public static boolean circlevsCircle(double x1, double y1, double r1, double x2, double y2, double r2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < (r1 + r2) * (r1 + r2);
	}

	public static boolean pointvsCircle(double x1, double y1, double x2, double y2, double r) {
		return circlevsPoint(x2, y2, r, x1, y1);
	}

	public static boolean circlevsPoint(double x1, double y1, double r, double x2, double y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < r * r;
	}

	public static boolean spherevsSphere(double x1, double y1, double z1, double r1, double x2, double y2, double z2,
			double r2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2) < (r1 + r2) * (r1 + r2);
	}

	public static boolean pointvsSphere(double x1, double y1, double z1, double x2, double y2, double z2, double r) {
		return spherevsPoint(x2, y2, z2, r, x1, y1, z1);
	}

	public static boolean spherevsPoint(double x1, double y1, double z1, double r, double x2, double y2, double z2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2) < r * r;
	}

	public static boolean quadvsQuad(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
		return (x1 - x2) * (x1 - x2) < (w1 + w2) * (w1 + w2) && (y1 - y2) * (y1 - y2) < (h1 + h2) * (h1 + h2);
	}

	public static boolean pointvsQuad(float x1, float y1, float x2, float y2, float w, float h) {
		return quadvsPoint(x2, y2, w, h, x1, y1);
	}

	public static boolean quadvsPoint(float x1, float y1, float w, float h, float x2, float y2) {
		return (x1 - x2) * (x1 - x2) < w * w && (y1 - y2) * (y1 - y2) < h * h;
	}

	public static boolean cubevsCube(float x1, float y1, float z1, float w1, float h1, float d1, float x2, float y2,
			float z2, float w2, float h2, float d2) {
		return (x1 - x2) * (x1 - x2) < (w1 + w2) * (w1 + w2) && (y1 - y2) * (y1 - y2) < (h1 + h2) * (h1 + h2)
				&& (z1 - z2) * (z1 - z2) < (d1 + d2) * (d1 + d2);
	}

	public static boolean pointvsCube(float x1, float y1, float z1, float x2, float y2, float z2, float w, float h,
			float d) {
		return cubevsPoint(x2, y2, z2, w, h, d, x1, y1, z1);
	}

	public static boolean cubevsPoint(float x1, float y1, float z1, float w, float h, float d, float x2, float y2,
			float z2) {
		return (x1 - x2) * (x1 - x2) < w * w && (y1 - y2) * (y1 - y2) < h * h && (z1 - z2) * (z1 - z2) < d * d;
	}

	public static boolean linevsLine(float xNorm1, float yNorm1, float xNorm2, float yNorm2) {
		return Vector2f.cross(xNorm1, yNorm1, xNorm2, yNorm2) != 0;
	}

	public static boolean pointvsLine(float x1, float y1, float x2, float y2, float xNorm, float yNorm) {
		return linevsPoint(x2, x2, xNorm, yNorm, x1, y1);
	}

	public static boolean linevsPoint(float x1, float y1, float xNorm, float yNorm, float x2, float y2) {
		return Vector2f.dot(xNorm, yNorm, x2 - x1, y2 - y1) > 0;
	}

	public static boolean planevsPlane(float xNorm1, float yNorm1, float zNorm1, float xNorm2, float yNorm2,
			float zNorm2) {
		Vector3f cross = Vector3f.cross(xNorm1, yNorm1, zNorm1, xNorm2, yNorm2, zNorm2);
		return cross.x != 0 && cross.y != 0 && cross.z != 0;
	}

	public static boolean pointvsPlane(float x1, float y1, float z1, float x2, float y2, float z2, float xNorm,
			float yNorm, float zNorm) {
		return planevsPoint(x2, x2, z2, xNorm, yNorm, zNorm, x1, y1, z1);
	}

	public static boolean planevsPoint(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm, float x2,
			float y2, float z2) {
		return Vector3f.dot(xNorm, yNorm, zNorm, x2 - x1, y2 - y1, z2 - z1) > 0;
	}

	public static boolean circlevsQuad(float x1, float y1, float r, float x2, float y2, float w, float h) {
		return quadvsCircle(x2, y2, w, h, x1, y1, r);
	}

	public static boolean quadvsCircle(float x1, float y1, float w, float h, float x2, float y2, float r) {
		return (x1 - x2) * (x1 - x2) < (w + r) * (w + r) && (y1 - y2) * (y1 - y2) < (h + r) * (h + r);
	}

	public static boolean spherevsCube(float x1, float y1, float z1, float r, float x2, float y2, float z2, float w,
			float h, float d) {
		return cubevsSphere(x2, y2, z2, w, h, d, x1, y1, z1, r);
	}

	public static boolean cubevsSphere(float x1, float y1, float z1, float w, float h, float d, float x2, float y2,
			float z2, float r) {
		return (x1 - x2) * (x1 - x2) < (w + r) * (w + r) && (y1 - y2) * (y1 - y2) < (h + r) * (h + r)
				&& (z1 - z2) * (z1 - z2) < (d + r) * (d + r);
	}

	public static boolean circlevsLine(float x1, float y1, float r, float x2, float y2, float xNorm, float yNorm) {
		return linevsCircle(x2, y2, xNorm, yNorm, x1, y1, r);
	}

	public static boolean linevsCircle(float x1, float y1, float xNorm, float yNorm, float x2, float y2, float r) {
		return Vector2f.dot(x2 - x1, y2 - y1, xNorm, yNorm) < r;
	}

	public static boolean spherevsPlane(float x1, float y1, float z1, float r, float x2, float y2, float z2,
			float xNorm, float yNorm, float zNorm) {
		return planevsSphere(x2, y2, z2, xNorm, yNorm, zNorm, x1, y1, z1, r);
	}

	public static boolean planevsSphere(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm, float x2,
			float y2, float z2, float r) {
		return Vector3f.dot(x2 - x1, y2 - y1, z2 - z1, xNorm, yNorm, zNorm) < r;
	}

	public static boolean quadvsLine(float x1, float y1, float w, float h, float x2, float y2, float xNorm,
			float yNorm) {
		return linevsQuad(x2, y2, xNorm, yNorm, x1, y1, w, h);
	}

	public static boolean linevsQuad(float x1, float y1, float xNorm, float yNorm, float x2, float y2, float w,
			float h) {
		return (x2 - x1) * xNorm < w && (y2 - y1) * yNorm < h;
	}

	public static boolean cubevsPlane(float x1, float y1, float z1, float w, float h, float d, float x2, float y2,
			float z2, float xNorm, float yNorm, float zNorm) {
		return planevsCube(x2, y2, z2, xNorm, yNorm, zNorm, x1, y1, z1, w, h, d);
	}

	public static boolean planevsCube(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm, float x2,
			float y2, float z2, float w, float h, float d) {
		return (x2 - x1) * xNorm < w && (y2 - y1) * yNorm < h && (z2 - z1) * zNorm < d;
	}

	public static boolean cylindervsCylinder(float x1, float y1, float z1, float r1, float h1, float x2, float y2,
			float z2, float r2, float h2) {
		return (x1 - x2) * (x1 - x2) < (r1 + r2) * (r1 + r2) && (y1 - y2) * (y1 - y2) < (h1 + h2) * (h1 + h2)
				&& (z1 - z2) * (z1 - z2) < (r1 + r2) * (r1 + r2);
	}

	public static boolean pointvsCylinder(float x1, float y1, float z1, float x2, float y2, float z2, float r,
			float h) {
		return cylindervsPoint(x2, y2, z2, r, h, x1, y1, z1);
	}

	public static boolean cylindervsPoint(float x1, float y1, float z1, float r, float h, float x2, float y2,
			float z2) {
		return (x1 - x2) * (x1 - x2) < r * r && (y1 - y2) * (y1 - y2) < h * h && (z1 - z2) * (z1 - z2) < r * r;
	}

	public static boolean spherevsCylinder(float x1, float y1, float z1, float r1, float x2, float y2, float z2,
			float r2, float h2) {
		return cylindervsSphere(x2, y2, z2, r2, h2, x1, y1, z1, r1);
	}

	public static boolean cylindervsSphere(float x1, float y1, float z1, float r1, float h1, float x2, float y2,
			float z2, float r2) {
		return (x1 - x2) * (x1 - x2) < (r1 + r2) * (r1 + r2) && (y1 - y2) * (y1 - y2) < (h1 + r2) * (h1 + r2)
				&& (z1 - z2) * (z1 - z2) < (r1 + r2) * (r1 + r2);
	}

	public static boolean cubevsCylinder(float x1, float y1, float z1, float w1, float h1, float d1, float x2, float y2,
			float z2, float r2, float h2) {
		return cylindervsCube(x2, y2, z2, r2, h2, x1, y1, z1, w1, h1, d1);
	}

	public static boolean cylindervsCube(float x1, float y1, float z1, float r1, float h1, float x2, float y2, float z2,
			float w2, float h2, float d2) {
		return (x1 - x2) * (x1 - x2) < (r1 + w2) * (r1 + w2) && (y1 - y2) * (y1 - y2) < (h1 + h2) * (h1 + h2)
				&& (z1 - z2) * (z1 - z2) < (r1 + d2) * (r1 + d2);
	}

	public static boolean planevsCylinder(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm, float x2,
			float y2, float z2, float r, float h) {
		return cylindervsPlane(x2, y2, z2, r, h, x1, y1, z1, xNorm, yNorm, zNorm);
	}

	public static boolean cylindervsPlane(float x1, float y1, float z1, float r, float h, float x2, float y2, float z2,
			float xNorm, float yNorm, float zNorm) {
		return (x2 - x1) * xNorm < r && (y2 - y1) * yNorm < h && (z2 - z1) * zNorm < r;
	}

	@Nullable
	public static CollisionData resolveCirclevsCircle(float x1, float y1, CircleCollider a, float x2, float y2,
			CircleCollider b) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float xPow = axisX * axisX;
		float yPow = axisY * axisY;
		float minDist = a.r + b.r;

		if (xPow + yPow < minDist * minDist) {
			float dist = FastMath.sqrt(xPow + yPow);
			float delta = minDist - dist;
			return new CollisionData(a, b, axisX, axisY, x1, y1, delta, delta, false);
		}
		return null;
	}

	@Nullable
	public static CollisionData resolveQuadvsQuad(float x1, float y1, AxisAlignedBoundingQuad a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = a.w + b.w;
		float minDistY = a.h + b.h;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new CollisionData(a, b, axisX, axisY, x1, y1, deltaX, deltaY, true);
		}
		return null;
	}

	@Nullable
	public static CollisionData resolveCirclevsQuad(float x1, float y1, CircleCollider a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		return resolveQuadvsCircle(x2, y2, b, x1, y1, a);
	}

	@Nullable
	public static CollisionData resolveQuadvsCircle(float x1, float y1, AxisAlignedBoundingQuad a, float x2, float y2,
			CircleCollider b) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = a.w + b.r;
		float minDistY = a.h + b.r;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			return new CollisionData(a, b, axisX, axisY, x1, y1, deltaX, deltaY, true);
		}
		return null;
	}

	@Nullable
	public static CollisionData resolveLinevsLine(float x1, float y1, LineCollider a, float x2, float y2,
			LineCollider b) {
		float dist = Vector2f.cross(a.xNorm, a.yNorm, b.xNorm, b.yNorm);
		if (dist != 0) {
			float axisX = a.xNorm - b.xNorm;
			float axisY = a.yNorm - b.yNorm;
			return new CollisionData(a, b, axisX, axisY, x2, y2, dist, dist, false);
		}
		return null;
	}

	@Nullable
	public static CollisionData resolveQuadvsLine(float x1, float y1, AxisAlignedBoundingQuad a, float x2, float y2,
			LineCollider b) {
		return resolveLinevsQuad(x2, y2, b, x1, y1, a);
	}

	@Nullable
	public static CollisionData resolveLinevsQuad(float x1, float y1, LineCollider a, float x2, float y2,
			AxisAlignedBoundingQuad b) {
		float distX = (x2 - x1) * a.xNorm;
		float distY = (y2 - y1) * a.yNorm;
		if (distX < b.w && distY < b.h) {
			float deltaX = b.w - distX;
			float deltaY = b.h - distY;
			return new CollisionData(a, b, -a.xNorm, -a.yNorm, x2, y2, deltaX, deltaY, false);
		}
		return null;
	}

	@Nullable
	public static CollisionData resolveCirclevsLine(float x1, float y1, CircleCollider a, float x2, float y2,
			LineCollider b) {
		return resolveLinevsCircle(x2, y2, b, x1, y1, a);
	}

	@Nullable
	public static CollisionData resolveLinevsCircle(float x1, float y1, LineCollider a, float x2, float y2,
			CircleCollider b) {
		float distX = (x2 - x1) * a.xNorm;
		float distY = (y2 - y1) * a.yNorm;
		if (distX + distY < b.r) {
			float deltaX = b.r - distX;
			float deltaY = b.r - distY;
			return new CollisionData(a, b, -a.xNorm, -a.yNorm, x2, y2, deltaX, deltaY, false);
		}
		return null;
	}

	public static boolean polygonvsPolygon(float x1, float y1, PolygonCollider a, float x2, float y2,
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
	public static CollisionData resolvePolygonvsPolygon(float x1, float y1, PolygonCollider a, float x2, float y2,
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
		return new CollisionData(sa, sb, collisionAxisX, collisionAxisY, 0, 0, collisionOverlap, collisionOverlap,
				false);
	}
}
