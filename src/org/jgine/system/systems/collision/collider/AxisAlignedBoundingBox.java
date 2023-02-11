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
import org.jgine.render.Renderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.system.systems.collision.CollisionChecks;

/**
 * Represents an AxisAlignedBoundingBox for 3D with float precision. An
 * AxisAlignedBoundingBox in 3D is represented by a center point, a width(w), a
 * height(h) and a depth(d).
 * 
 * @author Maximilian Paar
 */
public class AxisAlignedBoundingBox extends Collider {

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

	@Override
	public void scale(Vector3f scale) {
		w *= scale.x;
		h *= scale.y;
		d *= scale.z;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return (point.x >= (pos.x - this.w * 0.5f) && point.x <= (pos.x + this.w * 0.5f))
				&& (point.y >= (pos.y - this.h * 0.5f) && point.y <= (pos.y + this.h * 0.5f)
						&& (point.z >= (pos.z - this.d * 0.5f) && point.z <= (pos.z + this.d * 0.5f)));
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.checkAxisAlignedBoundingBoxvsAxisAlignedBoundingBox(pos, this, otherPos,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.checkAxisAlignedBoundingBoxvsBoundingSphere(pos, this, otherPos,
					(BoundingSphere) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.checkPlanevsAxisAlignedBoundingBox(otherPos, (PlaneCollider) other, pos, this);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.checkBoundingCylindervsAxisAlignedBoundingBox(otherPos, (BoundingCylinder) other,
					pos, this);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
	}

	@Override
	public AxisAlignedBoundingBox clone() {
		return new AxisAlignedBoundingBox(w, h, d);
	}

	@Override
	public void load(Map<String, Object> data) {
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
		w = in.readFloat();
		h = in.readFloat();
		d = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(w);
		out.writeFloat(h);
		out.writeFloat(d);
	}

	@Override
	public ColliderType<AxisAlignedBoundingBox> getType() {
		return ColliderTypes.BOX;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.renderCube(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(w, h, d)),
				new Material());
		Renderer.disableWireframeMode();
	}
}
