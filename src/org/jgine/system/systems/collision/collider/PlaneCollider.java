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

public class PlaneCollider extends Collider {

	public Vector3f normal;

	public PlaneCollider() {
		this.normal = Vector3f.NULL;
	}

	public PlaneCollider(Vector3f normal) {
		this.normal = normal;
	}

	@Override
	public void scale(Vector3f scale) {
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return distance(pos, point) > 0;
	}

	public float distance(Vector3f pos, Vector3f point) {
		Vector3f sub = Vector3f.sub(point, pos);
		return Vector3f.dot(normal, sub);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof PlaneCollider)
			return CollisionChecks.checkPlanevsPlane(pos, this, otherPos, (PlaneCollider) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.checkPlanevsBoundingSphere(pos, this, otherPos, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.checkPlanevsAxisAlignedBoundingBox(pos, this, otherPos,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.checkBoundingCylindervsPlane(otherPos, (BoundingCylinder) other, pos, this);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
	}

	@Override
	public PlaneCollider clone() {
		return new PlaneCollider(normal);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object normalData = data.get("normal");
		if (normalData != null)
			normal = YamlHelper.toVector3f(normalData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		normal = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(normal.x);
		out.writeFloat(normal.y);
		out.writeFloat(normal.z);
	}

	@Override
	public ColliderType<PlaneCollider> getType() {
		return ColliderTypes.PLANE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), pos, normal, new Vector3f(Float.MAX_VALUE)),
				new Material());
	}
}
