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

	public float xNorm;
	public float yNorm;
	public float zNorm;

	public PlaneCollider() {
	}

	public PlaneCollider(Vector3f normal) {
		this(normal.x, normal.y, normal.z);
	}

	public PlaneCollider(float xNorm, float yNorm, float zNorm) {
		this.xNorm = xNorm;
		this.yNorm = yNorm;
	}

	@Override
	public void scale(Vector3f scale) {
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return CollisionChecks.planevsPoint(pos.x, pos.y, pos.z, xNorm, yNorm, zNorm, point.x, point.y, point.z);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof PlaneCollider)
			return CollisionChecks.planevsPlane(xNorm, yNorm, zNorm, ((PlaneCollider) other).xNorm,
					((PlaneCollider) other).yNorm, ((PlaneCollider) other).zNorm);
		else if (other instanceof SphereCollider)
			return CollisionChecks.planevsSphere(pos.x, pos.y, pos.z, xNorm, yNorm, zNorm, otherPos.x, otherPos.y,
					otherPos.z, ((SphereCollider) other).r);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.planevsCube(pos.x, pos.y, pos.z, xNorm, yNorm, zNorm, otherPos.x, otherPos.y,
					otherPos.z, ((AxisAlignedBoundingBox) other).w, ((AxisAlignedBoundingBox) other).h,
					((AxisAlignedBoundingBox) other).d);
		else if (other instanceof CylinderCollider)
			return CollisionChecks.planevsCylinder(pos.x, pos.y, pos.z, xNorm, yNorm, zNorm, otherPos.x, otherPos.y,
					otherPos.z, ((CylinderCollider) other).r, ((CylinderCollider) other).h);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof PlaneCollider)
			return CollisionChecks.resolvePlanevsPlane(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(PlaneCollider) other);
		else if (other instanceof SphereCollider)
			return CollisionChecks.resolvePlanevsSphere(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(SphereCollider) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.resolvePlanevsCube(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(AxisAlignedBoundingBox) other);
		return null;
	}

	@Override
	public PlaneCollider clone() {
		return new PlaneCollider(xNorm, yNorm, zNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
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
		xNorm = in.readFloat();
		yNorm = in.readFloat();
		zNorm = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(xNorm);
		out.writeFloat(yNorm);
		out.writeFloat(zNorm);
	}

	@Override
	public ColliderType<PlaneCollider> getType() {
		return ColliderTypes.PLANE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), pos, new Vector3f(xNorm, yNorm, zNorm),
				new Vector3f(Float.MAX_VALUE)), new Material());
	}
}
