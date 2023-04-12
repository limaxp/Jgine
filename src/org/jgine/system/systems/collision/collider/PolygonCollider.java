package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
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

/**
 * Represents a PolygonCollider for 2D with float precision. A PolygonCollider
 * is represented by center point(x, y) and multiple corner points(x, y).
 */
public class PolygonCollider extends Collider {

	public float x;
	public float y;
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

	public PolygonCollider(float x, float y, Vector2f[] points) {
		this(points);
		this.x = x;
		this.y = y;
	}

	public PolygonCollider(float x, float y, float[] points) {
		this.x = x;
		this.y = y;
		this.points = points;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void scale(float x, float y, float z) {
		for (int i = 0; i < points.length; i += 2) {
			points[i] *= x;
			points[i + 1] *= y;
		}
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return false;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof PolygonCollider) {
			PolygonCollider o = (PolygonCollider) other;
			return CollisionChecks.polygonvsPolygon(x, y, this, o.x, o.y, o);
		}
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Collider other) {
		if (other instanceof PolygonCollider) {
			PolygonCollider o = (PolygonCollider) other;
			return CollisionChecks.resolvePolygonvsPolygon(x, y, this, o.x, o.y, o);
		}
		return null;
	}

	@Override
	public PolygonCollider clone() {
		return new PolygonCollider(x, y, points.clone());
	}

	@Override
	public void load(Map<String, Object> data) {
		Object points = data.get("points");
		if (points instanceof String) {
			String pointsString = (String) points;
			if (!pointsString.contains(","))
				return;
			String[] split = pointsString.split(",");
			this.points = new float[split.length];
			for (int i = 0; i < split.length; i++)
				this.points[i] = Float.parseFloat(split[i]);
		} else if (points instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> pointList = (List<Object>) points;
			this.points = new float[pointList.size()];
			for (int i = 0; i < pointList.size(); i++)
				this.points[i] = YamlHelper.toFloat(pointList.get(i));
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		int length = in.readInt();
		points = new float[length];
		for (int i = 0; i < length; i++)
			points[i] = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeInt(points.length);
		for (int i = 0; i < points.length; i++)
			out.writeFloat(points[i]);
	}

	@Override
	public ColliderType<PolygonCollider> getType() {
		return ColliderTypes.POLYGON;
	}

	@Override
	public void render() {
		Renderer2D.renderLine2d(Transform.calculateMatrix2d(new Matrix(), x, y, 1, 1), new Material(), true, points);
	}
}
