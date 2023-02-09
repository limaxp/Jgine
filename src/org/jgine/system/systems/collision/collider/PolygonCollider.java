package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.collision.CollisionData;

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
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
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
		int length = in.readInt();
		points = new float[length];
		for (int i = 0; i < length; i++)
			points[i] = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(points.length);
		for (int i = 0; i < points.length; i++)
			out.writeFloat(points[i]);
	}

	@Override
	public ColliderType<PolygonCollider> getType() {
		return ColliderTypes.POLYGON;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer2D.renderLine2d(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(1, 1, 0)),
				new Material(), true, points);
	}
}
