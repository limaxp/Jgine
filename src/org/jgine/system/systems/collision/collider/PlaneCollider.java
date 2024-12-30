package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents a PlaneCollider for 3D with float precision. A PlaneCollider is
 * represented by center point and normal vector(xNorm, yNorm, zNorm).
 */
public class PlaneCollider extends Collider {

	public float x;
	public float y;
	public float z;
	public float xNorm;
	public float yNorm;
	public float zNorm;

	public PlaneCollider() {
	}

	public PlaneCollider(float xNorm, float yNorm, float zNorm) {
		this.xNorm = xNorm;
		this.yNorm = yNorm;
		this.zNorm = zNorm;
	}

	public PlaneCollider(float x, float y, float z, float xNorm, float yNorm, float zNorm) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xNorm = xNorm;
		this.yNorm = yNorm;
		this.zNorm = zNorm;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Override
	public void scale(float x, float y, float z) {
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.planevsPoint(this.x, this.y, this.z, xNorm, yNorm, zNorm, x, y, z);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof PlaneCollider) {
			PlaneCollider o = (PlaneCollider) other;
			return CollisionChecks.planevsPlane(xNorm, yNorm, zNorm, o.xNorm, o.yNorm, o.zNorm);
		}

		else if (other instanceof SphereCollider) {
			SphereCollider o = (SphereCollider) other;
			return CollisionChecks.planevsSphere(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r);
		}

		else if (other instanceof AxisAlignedBoundingBox) {
			AxisAlignedBoundingBox o = (AxisAlignedBoundingBox) other;
			return CollisionChecks.planevsCube(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.w, o.h, o.d);
		}

		else if (other instanceof CylinderCollider) {
			CylinderCollider o = (CylinderCollider) other;
			return CollisionChecks.planevsCylinder(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r, o.h);
		}
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Collider other) {
		if (other instanceof PlaneCollider) {
			PlaneCollider o = (PlaneCollider) other;
			return CollisionChecks.resolvePlanevsPlane(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.xNorm, o.yNorm,
					o.zNorm);
		}

		else if (other instanceof SphereCollider) {
			SphereCollider o = (SphereCollider) other;
			return CollisionChecks.resolvePlanevsSphere(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r);
		}

		else if (other instanceof AxisAlignedBoundingBox) {
			AxisAlignedBoundingBox o = (AxisAlignedBoundingBox) other;
			return CollisionChecks.resolvePlanevsCube(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.w, o.h, o.d);
		}
		return null;
	}

	@Override
	public PlaneCollider clone() {
		return new PlaneCollider(x, y, z, xNorm, yNorm, zNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object zData = data.get("z");
		if (zData != null)
			z = YamlHelper.toFloat(zData);
		Object normalData = data.get("normal");
		if (normalData != null) {
			Vector3f normal = YamlHelper.toVector3f(normalData);
			xNorm = normal.x;
			yNorm = normal.y;
			zNorm = normal.z;
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		xNorm = in.readFloat();
		yNorm = in.readFloat();
		zNorm = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(xNorm);
		out.writeFloat(yNorm);
		out.writeFloat(zNorm);
	}

	@Override
	public ColliderType<PlaneCollider> getType() {
		return ColliderTypes.PLANE;
	}

	@Override
	public void render() {
		Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), x, y, z, xNorm, yNorm, zNorm, Float.MAX_VALUE,
				Float.MAX_VALUE, Float.MAX_VALUE), new Material());
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public float getWidth() {
		return Float.MAX_VALUE;
	}

	@Override
	public float getHeight() {
		return Float.MAX_VALUE;
	}

	@Override
	public float getDepth() {
		return Float.MAX_VALUE;
	}
}
