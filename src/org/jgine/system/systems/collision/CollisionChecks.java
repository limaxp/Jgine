package org.jgine.system.systems.collision;

import org.eclipse.jdt.annotation.Nullable;
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
	public static Collision resolveCirclevsCircle(float x1, float y1, float r1, float x2, float y2, float r2) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float xPow = axisX * axisX;
		float yPow = axisY * axisY;
		float minDist = r1 + r2;

		if (xPow + yPow < minDist * minDist) {
			float dist = FastMath.sqrt(xPow + yPow);
			float invDist = 1 / dist;
			float delta = minDist - dist;
			return new Collision(axisX * invDist, axisY * invDist, x1 + delta, y1 + delta, delta, delta);
		}
		return null;
	}

	@Nullable
	public static Collision resolveSpherevsSphere(float x1, float y1, float z1, float r1, float x2, float y2, float z2,
			float r2) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float axisZ = z1 - z2;
		float xPow = axisX * axisX;
		float yPow = axisY * axisY;
		float zPow = axisZ * axisZ;
		float minDist = r1 + r2;

		if (xPow + yPow + zPow < minDist * minDist) {
			float dist = FastMath.sqrt(xPow + yPow + zPow);
			float invDist = 1 / dist;
			float delta = minDist - dist;
			return new Collision(axisX * invDist, axisY * invDist, axisZ * invDist, x1 + delta, y1 + delta, z1 + delta,
					delta, delta, delta);
		}
		return null;
	}

	@Nullable
	public static Collision resolveQuadvsQuad(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = w1 + w2;
		float minDistY = h1 + h2;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			if (deltaX < deltaY) {
				axisX = Math.signum(axisX);
				axisY = 0.0f;
			} else {
				axisX = 0.0f;
				axisY = Math.signum(axisY);
			}
			return new Collision(axisX, axisY, x1 + deltaX, y1 + deltaY, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveCubevsCube(float x1, float y1, float z1, float w1, float h1, float d1, float x2,
			float y2, float z2, float w2, float h2, float d2) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float axisZ = z1 - z2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float distZ = Math.abs(axisZ);
		float minDistX = w1 + w2;
		float minDistY = h1 + h2;
		float minDistZ = d1 + d2;

		if (distX < minDistX && distY < minDistY && distZ < minDistZ) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			float deltaZ = minDistZ - distZ;
			if (deltaX < deltaY) {
				axisY = 0.0f;
				if (deltaX < deltaZ) {
					axisX = Math.signum(axisX);
					axisZ = 0.0f;
				} else {
					axisX = 0.0f;
					axisZ = Math.signum(axisZ);
				}
			} else {
				axisX = 0.0f;
				if (axisY < deltaZ) {
					axisY = Math.signum(axisY);
					axisZ = 0.0f;
				} else {
					axisY = 0.0f;
					axisZ = Math.signum(axisZ);
				}
			}
			return new Collision(axisX, axisY, axisZ, x1 + deltaX, y1 + deltaY, z1 + deltaZ, deltaX, deltaY, deltaZ);
		}
		return null;
	}

	@Nullable
	public static Collision resolveCirclevsQuad(float x1, float y1, float r, float x2, float y2, float w, float h) {
		Collision collision = resolveQuadvsCircle(x2, y2, w, h, x1, y1, r);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolveQuadvsCircle(float x1, float y1, float w, float h, float x2, float y2, float r) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float minDistX = w + r;
		float minDistY = h + r;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			if (deltaX < deltaY) {
				axisX = Math.signum(axisX);
				axisY = 0.0f;
			} else {
				axisX = 0.0f;
				axisY = Math.signum(axisY);
			}
			return new Collision(axisX, axisY, x1 + deltaX, y1 + deltaY, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveSpherevsCube(float x1, float y1, float z1, float r, float x2, float y2, float z2,
			float w, float h, float d) {
		Collision collision = resolveCubevsSphere(x2, y2, z2, w, h, d, x1, y1, z1, r);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolveCubevsSphere(float x1, float y1, float z1, float w, float h, float d, float x2,
			float y2, float z2, float r) {
		float axisX = x1 - x2;
		float axisY = y1 - y2;
		float axisZ = z1 - z2;
		float distX = Math.abs(axisX);
		float distY = Math.abs(axisY);
		float distZ = Math.abs(axisZ);
		float minDistX = w + r;
		float minDistY = h + r;
		float minDistZ = d + r;

		if (distX < minDistX && distY < minDistY && distZ < minDistZ) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			float deltaZ = minDistZ - distZ;
			if (deltaX < deltaY) {
				axisY = 0.0f;
				if (deltaX < deltaZ) {
					axisX = Math.signum(axisX);
					axisZ = 0.0f;
				} else {
					axisX = 0.0f;
					axisZ = Math.signum(axisZ);
				}
			} else {
				axisX = 0.0f;
				if (axisY < deltaZ) {
					axisY = Math.signum(axisY);
					axisZ = 0.0f;
				} else {
					axisY = 0.0f;
					axisZ = Math.signum(axisZ);
				}
			}
			return new Collision(axisX, axisY, axisZ, x1 + deltaX, y1 + deltaY, z1 + deltaZ, deltaX, deltaY, deltaZ);
		}
		return null;
	}

	@Nullable
	public static Collision resolveLinevsLine(float x1, float y1, float xNorm1, float yNorm1, float x2, float y2,
			float xNorm2, float yNorm2) {
		float dist = Vector2f.cross(xNorm1, yNorm1, xNorm2, yNorm2);
		if (dist != 0) {
			float axisX = xNorm1 - xNorm2;
			float axisY = yNorm1 - yNorm2;
			// TODO pos
			return new Collision(axisX, axisY, x1, y1, dist, dist);
		}
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsPlane(float x1, float y1, float z1, float xNorm1, float yNorm1, float zNorm1,
			float x2, float y2, float z2, float xNorm2, float yNorm2, float zNorm2) {
		Vector3f dist = Vector3f.cross(xNorm1, yNorm1, zNorm1, xNorm2, yNorm2, zNorm2);
		if (dist.x != 0 && dist.y != 0 && dist.z != 0) {
			float axisX = xNorm1 - xNorm2;
			float axisY = yNorm1 - yNorm2;
			float axisZ = zNorm1 - zNorm2;
			// TODO pos
			return new Collision(axisX, axisY, axisZ, x1, y1, z1, dist.x, dist.y, dist.z);
		}
		return null;
	}

	@Nullable
	public static Collision resolveQuadvsLine(float x1, float y1, float w, float h, float x2, float y2, float xNorm,
			float yNorm) {
		Collision collision = resolveLinevsQuad(x2, y2, xNorm, yNorm, x1, y1, w, h);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolveLinevsQuad(float x1, float y1, float xNorm, float yNorm, float x2, float y2, float w,
			float h) {
		float distX = (x2 - x1) * xNorm;
		float distY = (y2 - y1) * yNorm;
		if (distX < w && distY < h) {
			float deltaX = w - distX;
			float deltaY = h - distY;
			return new Collision(-xNorm, -yNorm, x1 + deltaX, y1 + deltaY, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveCubevsPlane(float x1, float y1, float z1, float w, float h, float d, float x2,
			float y2, float z2, float xNorm, float yNorm, float zNorm) {
		Collision collision = resolvePlanevsCube(x2, y2, z2, xNorm, yNorm, zNorm, x1, y1, z1, w, h, d);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsCube(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm,
			float x2, float y2, float z2, float w, float h, float d) {
		float distX = (x2 - x1) * xNorm;
		float distY = (y2 - y1) * yNorm;
		float distZ = (z2 - z1) * zNorm;
		if (distX < w && distY < h && distZ < d) {
			float deltaX = w - distX;
			float deltaY = h - distY;
			float deltaZ = d - distZ;
			return new Collision(-xNorm, -yNorm, -zNorm, x1 + deltaX, y1 + deltaY, z1 + deltaZ, deltaX, deltaY, deltaZ);
		}
		return null;
	}

	@Nullable
	public static Collision resolveCirclevsLine(float x1, float y1, float r, float x2, float y2, float xNorm,
			float yNorm) {
		Collision collision = resolveLinevsCircle(x2, y2, xNorm, yNorm, x1, y1, r);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolveLinevsCircle(float x1, float y1, float xNorm, float yNorm, float x2, float y2,
			float r) {
		float distX = (x2 - x1) * xNorm;
		float distY = (y2 - y1) * yNorm;
		if (distX + distY < r) {
			float deltaX = r - distX;
			float deltaY = r - distY;
			return new Collision(-xNorm, -yNorm, x1 + deltaX, y1 + deltaY, deltaX, deltaY);
		}
		return null;
	}

	@Nullable
	public static Collision resolveSpherevsPlane(float x1, float y1, float z1, float r, float x2, float y2, float z2,
			float xNorm, float yNorm, float zNorm) {
		Collision collision = resolvePlanevsSphere(x2, y2, z2, xNorm, yNorm, zNorm, x1, y1, z1, r);
		if (collision != null)
			return collision.reverse();
		return null;
	}

	@Nullable
	public static Collision resolvePlanevsSphere(float x1, float y1, float z1, float xNorm, float yNorm, float zNorm,
			float x2, float y2, float z2, float r) {
		float distX = (x2 - x1) * xNorm;
		float distY = (y2 - y1) * yNorm;
		float distZ = (z2 - z1) * zNorm;
		if (distX + distY + distZ < r) {
			float deltaX = r - distX;
			float deltaY = r - distY;
			float deltaZ = r - distZ;
			return new Collision(-xNorm, -yNorm, -zNorm, x1 + deltaX, y1 + deltaY, z1 + deltaZ, deltaX, deltaY, deltaZ);
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
		// TODO pos
		return new Collision(collisionAxisX, collisionAxisY, x1, y1, collisionOverlap, collisionOverlap);
	}
}
