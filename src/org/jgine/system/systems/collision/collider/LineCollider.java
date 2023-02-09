package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.collision.CollisionData;

public class LineCollider extends Collider {

	public Vector2f normal;

	public LineCollider() {
		this.normal = Vector2f.NULL;
	}

	public LineCollider(Vector2f normal) {
		this.normal = normal;
	}

	@Override
	public void scale(Vector3f scale) {
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return distance(pos, point) > 0;
	}

	public float distance(Vector2f pos, Vector2f point) {
		Vector2f sub = Vector2f.sub(point, pos);
		return Vector2f.dot(normal, sub);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks2D.checkLinevsLine(pos, this, otherPos, (LineCollider) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.checkLinevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.checkLinevsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks2D.resolveLinevsLine(pos, this, otherPos, (LineCollider) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.resolveLinevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.resolveLinevsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		return null;
	}

	@Override
	public LineCollider clone() {
		return new LineCollider(normal);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object normalData = data.get("normal");
		if (normalData != null)
			normal = YamlHelper.toVector2f(normalData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		normal = new Vector2f(in.readFloat(), in.readFloat());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(normal.x);
		out.writeFloat(normal.y);
	}

	@Override
	public ColliderType<LineCollider> getType() {
		return ColliderTypes.LINE;
	}

	@Override
	public void render(Vector3f pos) {
		// TODO
		Vector3f right = new Vector3f(-normal.y, normal.x, 0);
//		System.out.println(new Vector3f(normal) + "     " + right);
		Renderer2D.renderLine(
				Transform.calculateMatrix(new Matrix(), pos, new Vector3f(normal), new Vector3f(Float.MAX_VALUE)),
				new Material(Color.BLACK), 0, 0, 1, 1);
	}
}
