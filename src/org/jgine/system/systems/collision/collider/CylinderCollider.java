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
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents an BoundingCylinder for 3D with float precision. An
 * BoundingCylinder is represented by center point, radius(r) and height(h).
 */
public class CylinderCollider extends Collider {

	public float r;
	public float h;

	public CylinderCollider() {
	}

	public CylinderCollider(float r, float h) {
		this.r = r;
		this.h = h;
	}

	@Override
	public void scale(Vector3f scale) {
		if (scale.x == scale.z)
			r *= scale.x;
		else
			r *= (int) (scale.x + scale.z) / 2;
		h *= scale.y;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return CollisionChecks.cylindervsPoint(pos.x, pos.y, pos.z, r, h, point.x, point.y, point.z);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof CylinderCollider)
			return CollisionChecks.cylindervsCylinder(pos.x, pos.y, pos.z, r, h, otherPos.x, otherPos.y, otherPos.z,
					((CylinderCollider) other).r, ((CylinderCollider) other).h);
		else if (other instanceof SphereCollider)
			return CollisionChecks.cylindervsSphere(pos.x, pos.y, pos.z, r, h, otherPos.x, otherPos.y, otherPos.z,
					((SphereCollider) other).r);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.cylindervsCube(pos.x, pos.y, pos.z, r, h, otherPos.x, otherPos.y, otherPos.z,
					((AxisAlignedBoundingBox) other).w, ((AxisAlignedBoundingBox) other).h,
					((AxisAlignedBoundingBox) other).d);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.cylindervsPlane(pos.x, pos.y, pos.z, r, h, otherPos.x, otherPos.y, otherPos.z,
					((PlaneCollider) other).xNorm, ((PlaneCollider) other).yNorm, ((PlaneCollider) other).zNorm);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
	}

	@Override
	public CylinderCollider clone() {
		return new CylinderCollider(r, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object radiusData = data.get("radius");
		if (radiusData != null)
			r = YamlHelper.toFloat(radiusData);
		Object heightData = data.get("height");
		if (heightData != null)
			h = YamlHelper.toFloat(heightData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		r = in.readFloat();
		r = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(r);
		out.writeFloat(h);
	}

	@Override
	public ColliderType<CylinderCollider> getType() {
		return ColliderTypes.CYLINDER;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.render(Transform.calculateMatrix(new Matrix(), pos, new Vector3f(r, h, r)),
				ResourceManager.getModel("ball"));
		Renderer.disableWireframeMode();
	}
}
