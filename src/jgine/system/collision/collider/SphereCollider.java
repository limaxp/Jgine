package jgine.system.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.Renderer;
import jgine.system.collision.Collider;
import jgine.system.collision.ColliderType;
import jgine.system.collision.Collision;
import jgine.system.collision.CollisionChecks;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.Matrix;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * is represented by center point and radius(r).
 */
public class SphereCollider extends Collider {

	public float x;
	public float y;
	public float z;
	public float r;

	public SphereCollider() {
	}

	public SphereCollider(float r) {
		this.r = r;
	}

	public SphereCollider(float x, float y, float z, float r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
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
		this.r *= (x + y + z) * 0.3333333f;
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.spherevsPoint(this.x, this.y, this.z, r, x, y, z);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof SphereCollider o)
			return CollisionChecks.spherevsSphere(x, y, z, r, o.x, o.y, o.z, o.r);
		else if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.spherevsCube(x, y, z, r, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof PlaneCollider o)
			return CollisionChecks.spherevsPlane(x, y, z, r, o.x, o.y, o.z, o.xNorm, o.yNorm, o.zNorm);
		else if (other instanceof CylinderCollider o)
			return CollisionChecks.spherevsCylinder(x, y, z, r, o.x, o.y, o.z, o.r, o.h);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof SphereCollider o)
			return CollisionChecks.resolveSpherevsSphere(x, y, z, r, o.x, o.y, o.z, o.r);
		else if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.resolveSpherevsCube(x, y, z, r, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof PlaneCollider o)
			return CollisionChecks.resolveSpherevsPlane(x, y, z, r, o.x, o.y, o.z, o.xNorm, o.yNorm, o.zNorm);
		return null;
	}

	@Override
	public SphereCollider clone() {
		return new SphereCollider(x, y, z, r);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		z = ObjectUtils.toFloat(data.get("z"), z);
		r = ObjectUtils.toFloat(data.get("radius"), r);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("z", z);
		data.put("radius", r);
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		r = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(r);
	}

	@Override
	public ColliderType<SphereCollider> getType() {
		return ColliderType.SPHERE;
	}

	@Override
	public void render() {
		Renderer.render(Transform.calculateMatrix(new Matrix(), x, y, z, r, r, r), ResourceManager.getModel("ball"));
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
		return r;
	}

	@Override
	public float getHeight() {
		return r;
	}

	@Override
	public float getDepth() {
		return r;
	}
}
