package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionChecks2D;

public class PolygonCollider extends Collider {

	public float[] points;

	public PolygonCollider() {
	}

	public PolygonCollider(Vector2f[] points) {
		this.points = new float[points.length * 2];
		int index = 0;
		for (int i = 0; i < points.length; i++) {
			Vector2f point = points[i];
			this.points[index] = point.x;
			this.points[index + 1] = point.y;
			index += 2;
		}
	}

	public PolygonCollider(float[] points) {
		this.points = points;
	}

	@Override
	public void scale(Vector3f scale) {
		for (int i = 0; i < points.length; i += 2) {
			points[i] *= scale.x;
			points[i + 1] *= scale.y;
		}
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return false;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof PolygonCollider)
			return CollisionChecks2D.resolvePolygonvsPolygon(pos, this, otherPos, (PolygonCollider) other);
		return null;
	}

	@Override
	public PolygonCollider clone() {
		return new PolygonCollider(points.clone());
	}

	@Override
	public void load(Map<String, Object> data) {
		Object points = data.get("points");
		if (points != null && points instanceof String) {
			String pointsString = (String) points;
			if (!pointsString.contains(","))
				return;
			String[] split = pointsString.split(",");
			this.points = new float[split.length];
			for (int i = 0; i < split.length; i++)
				this.points[i] = Float.parseFloat(split[i]);
		}
	}

	@Override
	public ColliderType<PolygonCollider> getType() {
		return ColliderTypes.POLYGON;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer2D.renderLine2d(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(1, 1, 0)),
				points, new Material(), true);
	}
}
