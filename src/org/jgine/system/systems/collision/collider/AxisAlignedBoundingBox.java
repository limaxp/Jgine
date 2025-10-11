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
import org.jgine.system.systems.collision.Collision;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;

/**
 * Represents an AxisAlignedBoundingBox for 3D with float precision. An
 * AxisAlignedBoundingBox is represented by center point, width(w), height(h)
 * and depth(d).
 */
public class AxisAlignedBoundingBox extends Collider {

	public float x;
	public float y;
	public float z;
	public float w;
	public float h;
	public float d;

	public AxisAlignedBoundingBox() {
	}

	public AxisAlignedBoundingBox(float w, float h, float d) {
		this.w = w;
		this.h = h;
		this.d = d;
	}

	public AxisAlignedBoundingBox(float x, float y, float z, float w, float h, float d) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.h = h;
		this.d = d;
	}

	@Override
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Override
	public void scale(float x, float y, float z) {
		this.w *= x;
		this.h *= y;
		this.d *= z;
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.cubevsPoint(this.x, this.y, this.z, w, h, d, x, y, z);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.cubevsCube(x, y, z, w, h, d, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof SphereCollider o)
			return CollisionChecks.cubevsSphere(x, y, z, w, h, d, o.x, o.y, o.z, o.r);
		else if (other instanceof PlaneCollider o)
			return CollisionChecks.cubevsPlane(x, y, z, w, h, d, o.x, o.y, o.z, o.xNorm, o.yNorm, o.zNorm);
		else if (other instanceof CylinderCollider o)
			return CollisionChecks.cubevsCylinder(x, y, z, w, h, d, o.x, o.y, o.z, o.r, o.h);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.resolveCubevsCube(x, y, z, w, h, d, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof SphereCollider o)
			return CollisionChecks.resolveCubevsSphere(x, y, z, w, h, d, o.x, o.y, o.z, o.r);
		else if (other instanceof PlaneCollider o)
			return CollisionChecks.resolveCubevsPlane(x, y, z, w, h, d, o.x, o.y, o.z, o.xNorm, o.yNorm, o.zNorm);
		return null;
	}

	@Override
	public AxisAlignedBoundingBox clone() {
		return new AxisAlignedBoundingBox(x, y, z, w, h, d);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object zData = data.get("z");
		if (zData != null)
			z = YamlHelper.toFloat(zData);
		Object widthData = data.get("width");
		if (widthData != null)
			w = YamlHelper.toFloat(widthData);
		Object heightData = data.get("height");
		if (heightData != null)
			h = YamlHelper.toFloat(heightData);
		Object depthData = data.get("depth");
		if (depthData != null)
			d = YamlHelper.toFloat(depthData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		w = in.readFloat();
		h = in.readFloat();
		d = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(w);
		out.writeFloat(h);
		out.writeFloat(d);
	}

	@Override
	public ColliderType<AxisAlignedBoundingBox> getType() {
		return ColliderTypes.BOX;
	}

	@Override
	public void render() {
		Renderer.renderCube(Transform.calculateMatrix(new Matrix(), x, y, z, w, h, d), new Material());
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
		return z;
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
		return d;
	}
}
