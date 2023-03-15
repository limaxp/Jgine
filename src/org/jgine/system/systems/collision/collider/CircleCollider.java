package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents a BoundingCircle for 2D with float precision. A BoundingCircle is
 * represented by center point and radius(r).
 */
public class CircleCollider extends Collider {

	public float r;

	public CircleCollider() {
	}

	public CircleCollider(float r) {
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
		return CollisionChecks.circlevsPoint(pos.x, pos.y, r, point.x, point.y);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof CircleCollider)
			return CollisionChecks.circlevsCircle(pos.x, pos.y, r, otherPos.x, otherPos.y, ((CircleCollider) other).r);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.circlevsQuad(pos.x, pos.y, r, otherPos.x, otherPos.y,
					((AxisAlignedBoundingQuad) other).w, ((AxisAlignedBoundingQuad) other).h);
		else if (other instanceof LineCollider)
			return CollisionChecks.circlevsLine(pos.x, pos.y, r, otherPos.x, otherPos.y, ((LineCollider) other).xNorm,
					((LineCollider) other).yNorm);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof CircleCollider)
			return CollisionChecks.resolveCirclevsCircle(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(CircleCollider) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.resolveCirclevsQuad(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(AxisAlignedBoundingQuad) other);
		else if (other instanceof LineCollider)
			return CollisionChecks.resolveCirclevsLine(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(LineCollider) other);
		return null;
	}

	@Override
	public CircleCollider clone() {
		return new CircleCollider(r);
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
	public ColliderType<CircleCollider> getType() {
		return ColliderTypes.CIRCLE;
	}

	@Override
	public void render(Vector3f pos) {
		float points[] = new float[32 * 2];
		float angle = (float) FastMath.PI2 / 32;
		int i = 0;
		for (float phi = 0; phi < FastMath.PI2; phi += angle) {
			points[i] = FastMath.sin(phi);
			points[i + 1] = FastMath.cos(phi);
			i += 2;
		}
		Renderer2D.renderLine2d(Transform.calculateMatrix2d(new Matrix(), pos, new Vector2f(r, r)), new Material(),
				true, points);
	}
}
