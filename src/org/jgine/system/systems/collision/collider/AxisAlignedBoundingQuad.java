package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.MeshGenerator;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;

/**
 * Represents an AxisAlignedBoundingQuad for 2D with float precision. An
 * AxisAlignedBoundingQuad is represented by center point(x, y), width(w) and
 * height(h).
 */
public class AxisAlignedBoundingQuad extends Collider {

	public static final BaseMesh COLLIDER_MESH = MeshGenerator.quadHollow(1.0f);

	public float x;
	public float y;
	public float w;
	public float h;

	public AxisAlignedBoundingQuad() {
	}

	public AxisAlignedBoundingQuad(float w, float h) {
		this.w = w;
		this.h = h;
	}

	public AxisAlignedBoundingQuad(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
	}

	@Override
	public void scale(float x, float y, float z) {
		this.w *= x;
		this.h *= y;
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.quadvsPoint(this.x, this.y, this.w, this.h, x, y);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.quadvsQuad(x, y, w, h, o.x, o.y, o.w, o.h);
		}

		else if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.quadvsCircle(x, y, w, h, o.x, o.y, o.r);
		}

		else if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.quadvsLine(x, y, w, h, o.x, o.y, o.xNorm, o.yNorm);
		}
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Collider other) {
		if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.resolveQuadvsQuad(x, y, w, h, o.x, o.y, o.w, o.h);
		}

		else if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.resolveQuadvsCircle(x, y, w, h, o.x, o.y, o.r);
		}

		else if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.resolveQuadvsLine(x, y, w, h, o.x, o.y, o.xNorm, o.yNorm);
		}
		return null;
	}

	@Override
	public AxisAlignedBoundingQuad clone() {
		return new AxisAlignedBoundingQuad(x, y, w, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object widthData = data.get("width");
		if (widthData != null)
			w = YamlHelper.toFloat(widthData);
		Object heightData = data.get("height");
		if (heightData != null)
			h = YamlHelper.toFloat(heightData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		w = in.readFloat();
		h = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(w);
		out.writeFloat(h);
	}

	@Override
	public ColliderType<AxisAlignedBoundingQuad> getType() {
		return ColliderTypes.QUAD;
	}

	@Override
	public void render() {
		Renderer2D.render(Transform.calculateMatrix2d(new Matrix(), x, y, w, h), COLLIDER_MESH, Renderer.BASIC_SHADER,
				new Material());
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return 0.0f;
	}

	@Override
	public float getWidth() {
		return w;
	}

	@Override
	public float getHeight() {
		return h;
	}

	@Override
	public float getDepth() {
		return 0.0f;
	}
}
