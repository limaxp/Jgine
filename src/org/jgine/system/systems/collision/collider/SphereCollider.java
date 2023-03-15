package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.Renderer;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.system.systems.collision.CollisionChecks;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * is represented by center point and radius(r).
 */
public class SphereCollider extends Collider {

	public float r;

	public SphereCollider() {
	}

	public SphereCollider(float r) {
		this.r = r;
	}

	@Override
	public void scale(Vector3f scale) {
		if (scale.x == scale.y && scale.y == scale.z)
			r *= scale.x;
		else
			r *= (int) (scale.x + scale.y + scale.z) / 3;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return CollisionChecks.spherevsPoint(pos.x, pos.y, pos.z, r, point.x, point.y, point.z);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof SphereCollider)
			return CollisionChecks.spherevsSphere(pos.x, pos.y, pos.z, r, otherPos.x, otherPos.y, otherPos.z,
					((SphereCollider) other).r);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.spherevsCube(pos.x, pos.y, pos.z, r, otherPos.x, otherPos.y, otherPos.z,
					((AxisAlignedBoundingBox) other).w, ((AxisAlignedBoundingBox) other).h,
					((AxisAlignedBoundingBox) other).d);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.spherevsPlane(pos.x, pos.y, pos.z, r, otherPos.x, otherPos.y, otherPos.z,
					((PlaneCollider) other).xNorm, ((PlaneCollider) other).yNorm, ((PlaneCollider) other).zNorm);
		else if (other instanceof CylinderCollider)
			return CollisionChecks.spherevsCylinder(pos.x, pos.y, pos.z, r, otherPos.x, otherPos.y, otherPos.z,
					((CylinderCollider) other).r, ((CylinderCollider) other).h);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof SphereCollider)
			return CollisionChecks.resolveSpherevsSphere(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(SphereCollider) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.resolveSpherevsCube(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.resolveSpherevsPlane(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(PlaneCollider) other);
		return null;
	}

	@Override
	public SphereCollider clone() {
		return new SphereCollider(r);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object radiusData = data.get("radius");
		if (radiusData != null)
			r = YamlHelper.toFloat(radiusData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		r = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(r);
	}

	@Override
	public ColliderType<SphereCollider> getType() {
		return ColliderTypes.SPHERE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.render(Transform.calculateMatrix(new Matrix(), pos, new Vector3f(r)),
				ResourceManager.getModel("ball"));
		Renderer.disableWireframeMode();
	}
}
