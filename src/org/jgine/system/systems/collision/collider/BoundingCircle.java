package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.system.systems.collision.CollisionChecks2D;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * in 3D is represented by a center point and a radius(r).
 * 
 * @author Maximilian Paar
 */
public class BoundingCircle extends Collider {

	public float r;

	public BoundingCircle() {
	}

	public BoundingCircle(float r) {
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
		return Vector3f.distance(pos, point) < r * r;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingCircle)
			return CollisionChecks2D.checkBoundingCirclevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.checkAxisAlignedBoundingQuadvsBoundingCircle(otherPos,
					(AxisAlignedBoundingQuad) other, pos, this);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.checkLinevsBoundingCircle(otherPos, (LineCollider) other, pos, this);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingCircle)
			return CollisionChecks2D.resolveBoundingCirclevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(otherPos,
					(AxisAlignedBoundingQuad) other, pos, this);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.resolveLinevsBoundingCircle(otherPos, (LineCollider) other, pos, this);
		return null;
	}

	@Override
	public BoundingCircle clone() {
		return new BoundingCircle(r);
	}

	@Override
	public void load(Map<String, Object> data) {
		r = YamlHelper.toFloat(data.get("radius"));
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
	public ColliderType<BoundingCircle> getType() {
		return ColliderTypes.CIRCLE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer2D.renderCircle(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(r)),
				new Material());
	}
}
