package jgine.system.transform;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.system.SystemObject;
import jgine.utils.ObjectUtils;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.math.Matrix;
import jgine.utils.math.rotation.AxisAngle4f;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.spacePartitioning.SpacePartitioning;

/**
 * A transform with float precision. Uses {@link Matrix} class internally. This
 * is your primary way to change entity position, rotation and scale. Also
 * stores a reference to its {@link Entity} and the scenes
 * {@link SpacePartitioning}.
 */
public class Transform implements SystemObject {

	private Entity entity;
	private float posX;
	private float posY;
	private float posZ;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float scaleX;
	private float scaleY;
	private float scaleZ;
	private Matrix matrix;
	private boolean isDirty;
	private Transform parent;
	private List<Transform> childs;

	public Transform() {
		clear();
		this.matrix = new Matrix();
		this.childs = Collections.synchronizedList(new UnorderedIdentityArrayList<>(0));
	}

	void free() {
		childs = null;
	}

	public void clear() {
		posX = 0.0f;
		posY = 0.0f;
		posZ = 0.0f;
		rotX = 0.0f;
		rotY = 0.0f;
		rotZ = 0.0f;
		scaleX = 1.0f;
		scaleY = 1.0f;
		scaleZ = 1.0f;
		markDirty();
	}

	private void markDirty() {
		isDirty = true;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public final void calculateMatrix() {
		isDirty = false;
		calculateMatrix(matrix, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		if (hasParent())
			matrix.mult(getParent().getMatrix());
		for (Transform child : getChilds())
			child.calculateMatrix();
	}

	public final Matrix getMatrix() {
		return matrix;
	}

	public final void setPosition(Vector2f position) {
		setPosition(position.x, position.y, 0.0f);
	}

	public final void setPosition(float x, float y) {
		setPosition(x, y, 0.0f);
	}

	public final void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}

	public final void setPosition(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
		markDirty();
	}

	public final void movePosition(float dx, float dy, float dz) {
		posX += dx;
		posY += dy;
		posZ += dz;
		markDirty();
	}

	public final Vector3f getPosition() {
		return new Vector3f(matrix.m03, matrix.m13, matrix.m23);
	}

	public final float getX() {
		return matrix.m03;
	}

	public final float getY() {
		return matrix.m13;
	}

	public final float getZ() {
		return matrix.m23;
	}

	public final Vector3f getLocalPosition() {
		return new Vector3f(posX, posY, posZ);
	}

	public final float getLocalX() {
		return posX;
	}

	public final float getLocalY() {
		return posY;
	}

	public final float getLocalZ() {
		return posZ;
	}

	public final void setScale(float scale) {
		setScale(scale, scale, scale);
	}

	public final void setScale(Vector2f scale) {
		setScale(scale.x, scale.y, 0.0f);
	}

	public final void setScale(float x, float y) {
		setScale(x, y, 0.0f);
	}

	public final void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);
	}

	public final void setScale(float x, float y, float z) {
		scaleX = x;
		scaleY = y;
		scaleZ = z;
		markDirty();
	}

	public final Vector3f getScale() {
		return new Vector3f(matrix.m00, matrix.m11, matrix.m22);
	}

	public final float getScaleX() {
		return matrix.m00;
	}

	public final float getScaleY() {
		return matrix.m11;
	}

	public final float getScaleZ() {
		return matrix.m22;
	}

	public final Vector3f getLocalScale() {
		return new Vector3f(scaleX, scaleY, scaleZ);
	}

	public final float getLocalScaleX() {
		return scaleX;
	}

	public final float getLocalScaleY() {
		return scaleY;
	}

	public final float getLocalScaleZ() {
		return scaleZ;
	}

	public final void setRotation(Vector2f rotation) {
		setRotation(rotation.x, rotation.y);
	}

	public final void setRotation(float x, float y) {
		rotX = x;
		rotY = y;
		markDirty();
	}

	public final void setRotation(Vector3f rotation) {
		setRotation(rotation.x, rotation.y, rotation.z);
	}

	public final void setRotation(float x, float y, float z) {
		rotX = x;
		rotY = y;
		rotZ = z;
		markDirty();
	}

	public final AxisAngle4f getRotation() {
		return matrix.getRotation(new AxisAngle4f());
	}

	public final Vector3f getLocalRotation() {
		return new Vector3f(rotX, rotY, rotZ);
	}

	public final float getLocalRotationX() {
		return rotX;
	}

	public final float getLocalRotationY() {
		return rotY;
	}

	public final float getLocalRotationZ() {
		return rotZ;
	}

	public final void rotateX(float angle) {
		Vector3f rotation = getLocalRotation();
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, rotation));
		Vector3f result = Vector3f.normalize(Vector3f.rotate(rotation, angle, hAxis));
		setRotation(result.x, result.y, result.z);
	}

	public final void rotateY(float angle) {
		Vector3f rotation = getLocalRotation();
		Vector3f result = Vector3f.normalize(Vector3f.rotate(rotation, angle, Vector3f.Y_AXIS));
		setRotation(result.x, result.y, result.z);
	}

	void setEntity(Entity entity) {
		this.entity = entity;
	}

	public final Entity getEntity() {
		return entity;
	}

	public final void load(DataInput in) throws IOException {
		posX = in.readFloat();
		posY = in.readFloat();
		posZ = in.readFloat();
		rotX = in.readFloat();
		rotY = in.readFloat();
		rotZ = in.readFloat();
		scaleX = in.readFloat();
		scaleY = in.readFloat();
		scaleZ = in.readFloat();
		markDirty();

		// TODO rebuild graph
//		int childSize = in.readInt();
//		for (int i = 0; i < childSize; i++) {
//			Transform child = new Transform();
//			child.load(in);
//			addChild(child);
//		}
	}

	public final void save(DataOutput out) throws IOException {
		out.writeFloat(posX);
		out.writeFloat(posY);
		out.writeFloat(posZ);
		out.writeFloat(rotX);
		out.writeFloat(rotY);
		out.writeFloat(rotZ);
		out.writeFloat(scaleX);
		out.writeFloat(scaleY);
		out.writeFloat(scaleZ);

		// TODO rebuild graph
//		int childSize = childs.size();
//		out.writeInt(childSize);
//		for (int i = 0; i < childSize; i++)
//			childs.get(i).save(out);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object position = data.get("position");
		if (position instanceof List list) {
			if (list.size() >= 3) {
				posX = ObjectUtils.toFloat(list.get(0));
				posY = ObjectUtils.toFloat(list.get(1));
				posZ = ObjectUtils.toFloat(list.get(2));
			}
		}
		Object rotation = data.get("rotation");
		if (rotation instanceof List list) {
			if (list.size() >= 3) {
				rotX = ObjectUtils.toFloat(list.get(0));
				rotY = ObjectUtils.toFloat(list.get(1));
				rotZ = ObjectUtils.toFloat(list.get(2));
			}
		}
		Object scale = data.get("scale");
		if (scale instanceof Number)
			scaleX = scaleY = scaleZ = ((Number) scale).floatValue();
		else if (scale instanceof List list) {
			if (list.size() >= 3) {
				scaleX = ObjectUtils.toFloat(list.get(0));
				scaleY = ObjectUtils.toFloat(list.get(1));
				scaleZ = ObjectUtils.toFloat(list.get(2));
			}
		}
		markDirty();
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("position", Arrays.asList(posX, posY, posZ));
		data.put("rotation", Arrays.asList(rotX, rotY, rotZ));
		data.put("scale", Arrays.asList(scaleX, scaleY, scaleZ));
	}

	@Override
	public int system() {
		return Engine.TRANSFORM;
	}

	@Override
	public Transform clone() {
		try {
			Transform obj = (Transform) super.clone();
			obj.matrix = new Matrix(matrix);
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "[pos=" + posX + "," + posY + "," + posZ + " | rot=" + rotX + "," + rotY + "," + rotZ + " | scale="
				+ scaleX + "," + scaleY + "," + scaleZ + "]";
	}

	public final void setParent(@Nullable Transform parent) {
		if (this.parent != null)
			this.parent.childs.remove(this);
		if (parent != null)
			parent.childs.add(this);
		this.parent = parent;
		markDirty();
	}

	@Nullable
	public final Transform getParent() {
		return parent;
	}

	public final boolean hasParent() {
		return parent != null;
	}

	public final void addChild(Transform child) {
		if (child.parent != null)
			child.parent.childs.remove(child);
		child.parent = this;
		childs.add(child);
		child.markDirty();
	}

	public final void removeChild(Transform child) {
		if (child.parent != this)
			return;
		childs.remove(child);
		child.parent = null;
		child.markDirty();
	}

	public final void isChild(Transform child) {
		childs.contains(child);
	}

	public final void clearChilds() {
		for (Transform child : childs) {
			child.parent = null;
			child.markDirty();
		}
		childs.clear();
	}

	public final void forChilds(Consumer<Transform> consumer) {
		int n = childs.size();
		for (int i = 0; i < n; i++)
			consumer.accept(childs.get(i));
	}

	public final List<Transform> getChilds() {
		return Collections.unmodifiableList(childs);
	}

	public static Matrix calculateMatrix2d(Matrix matrix, Vector2f position, Vector2f rotation, Vector2f scale) {
		return calculateMatrix2d(matrix, position.x, position.y, rotation.x, rotation.y, scale.x, scale.y);
	}

	public static Matrix calculateMatrix2d(Matrix matrix, float posX, float posY, float rotX, float rotY, float scaleX,
			float scaleY) {
		matrix.clear();
		matrix.setPosition(posX, posY, 0.0f);
		matrix.rotationXY(rotX, rotY);
		matrix.scaleLocal(scaleX, scaleY, 0.0f); // return new Matrix().scaling(scale).mult(matrix);
		return matrix;
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f rotation, Vector3f scale) {
		return calculateMatrix(matrix, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x,
				scale.y, scale.z);
	}

	public static Matrix calculateMatrix(Matrix matrix, float posX, float posY, float posZ, float rotX, float rotY,
			float rotZ, float scaleX, float scaleY, float scaleZ) {
		matrix.clear();
		matrix.setPosition(posX, posY, posZ);
		matrix.rotationXYZ(rotX, rotY, rotZ);
		matrix.scaleLocal(scaleX, scaleY, scaleZ); // return new Matrix().scaling(scale).mult(matrix);
		return matrix;
	}

	public static Matrix calculateMatrix2d(Matrix matrix, Vector2f position, Vector2f scale) {
		return calculateMatrix2d(matrix, position.x, position.y, scale.x, scale.y);
	}

	public static Matrix calculateMatrix2d(Matrix matrix, float posX, float posY, float scaleX, float scaleY) {
		matrix.clear();
		matrix.setPosition(posX, posY, 0.0f);
		matrix.scaleLocal(scaleX, scaleY, 0.0f);
		return matrix;
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f scale) {
		return calculateMatrix(matrix, position.x, position.y, position.z, scale.x, scale.y, scale.z);
	}

	public static Matrix calculateMatrix(Matrix matrix, float posX, float posY, float posZ, float scaleX, float scaleY,
			float scaleZ) {
		matrix.clear();
		matrix.setPosition(posX, posY, posZ);
		matrix.scaleLocal(scaleX, scaleY, scaleZ);
		return matrix;
	}
}
