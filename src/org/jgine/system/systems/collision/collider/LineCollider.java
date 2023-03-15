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
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents a LineCollider for 2D with float precision. A LineCollider is
 * represented by center point and normal vector(xNorm, yNorm).
 */
public class LineCollider extends Collider {

	public float xNorm;
	public float yNorm;

	public LineCollider() {
	}

	public LineCollider(Vector2f normal) {
		this(normal.x, normal.y);
	}

	public LineCollider(float xNorm, float yNorm) {
		this.xNorm = xNorm;
		this.yNorm = yNorm;
	}

	@Override
	public void scale(Vector3f scale) {
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return CollisionChecks.linevsPoint(pos.x, pos.y, xNorm, yNorm, point.x, point.y);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks.linevsLine(xNorm, yNorm, ((LineCollider) other).xNorm, ((LineCollider) other).yNorm);
		else if (other instanceof CircleCollider)
			return CollisionChecks.linevsCircle(pos.x, pos.y, xNorm, yNorm, otherPos.x, otherPos.y,
					((CircleCollider) other).r);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.linevsQuad(pos.x, pos.y, xNorm, yNorm, otherPos.x, otherPos.y,
					((AxisAlignedBoundingQuad) other).w, ((AxisAlignedBoundingQuad) other).h);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks.resolveLinevsLine(pos.x, pos.y, this, otherPos.x, otherPos.y, (LineCollider) other);
		else if (other instanceof CircleCollider)
			return CollisionChecks.resolveLinevsCircle(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(CircleCollider) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.resolveLinevsQuad(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(AxisAlignedBoundingQuad) other);
		return null;
	}

	@Override
	public LineCollider clone() {
		return new LineCollider(xNorm, yNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object normalData = data.get("normal");
		if (normalData != null) {
			Vector2f normal = YamlHelper.toVector2f(normalData);
			xNorm = normal.x;
			yNorm = normal.y;
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		xNorm = in.readFloat();
		yNorm = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(xNorm);
		out.writeFloat(yNorm);
	}

	@Override
	public ColliderType<LineCollider> getType() {
		return ColliderTypes.LINE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer2D.renderLine(Transform.calculateMatrix2d(new Matrix(), pos, new Vector2f(Float.MAX_VALUE)),
				new Material(), yNorm, -xNorm, -yNorm, xNorm);
	}
}
