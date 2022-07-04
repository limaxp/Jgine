package org.jgine.misc.math;

import org.jgine.misc.math.vector.Vector3f;

/**
 * Represents a Transform for 3D with float precision.
 * 
 * @author Maximilian Paar
 */
public class Transform3f implements Cloneable {

	protected boolean hasChanged;
	protected Vector3f position;
	protected Vector3f rotation;
	protected Vector3f scale;
	protected Matrix matrix;

	public Transform3f() {
		this(Vector3f.NULL, Vector3f.NULL, Vector3f.FULL);
	}

	public Transform3f(Vector3f pos) {
		this(pos, Vector3f.NULL, Vector3f.FULL);
	}

	public Transform3f(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		matrix = new Matrix();
		matrix = calculateMatrix(matrix, position, rotation, scale);
	}

	@Override
	public Transform3f clone() {
		try {
			Transform3f obj = (Transform3f) super.clone();
			obj.matrix = new Matrix();
			obj.hasChanged = true;
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void calculateMatrix() {
		if (!hasChanged)
			return;
		matrix = calculateMatrix(matrix, position, rotation, scale);
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f rotation, Vector3f scale) {
		matrix.clear();
		matrix.setPosition(position);
		matrix.rotationXYZ(rotation.x, rotation.y, rotation.z);
		matrix.scaleLocal(scale); // return new Matrix().scaling(scale).mult(matrix);
		return matrix;
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f scale) {
		matrix.clear();
		matrix.setPosition(position);
		matrix.scaleLocal(scale);
		return matrix;
	}

	public final Matrix getMatrix() {
		return matrix;
	}

	public void setPosition(Vector3f position) {
		setHasChanged();
		this.position = position;
	}

	public void setPositionNoUpdate(Vector3f position) {
		setHasChanged();
		this.position = position;
	}

	public final Vector3f getPosition() {
		return matrix.getPosition();
	}

	public final Vector3f getLocalPosition() {
		return this.position;
	}

	public final void setRotation(Vector3f rotation) {
		setHasChanged();
		this.rotation = rotation;
	}

	public final Vector3f getRotation() {
		// TODO implement this
		return matrix.getRotation();
	}

	public final Vector3f getLocalRotation() {
		return rotation;
	}

	public final void setScale(Vector3f scale) {
		setHasChanged();
		this.scale = scale;
	}

	public final void setScale(float scale) {
		setScale(new Vector3f(scale));
	}

	public final Vector3f getScale() {
		return matrix.getScale();
	}

	public final Vector3f getLocalScale() {
		return scale;
	}

	public final void setHasChanged() {
		this.hasChanged = true;
	}

	public final boolean hasChanged() {
		return hasChanged;
	}

	public final void rotateX(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, rotation));
		setRotation(Vector3f.normalize(Vector3f.rotate(rotation, angle, hAxis)));
	}

	public final void rotateY(float angle) {
		setRotation(Vector3f.normalize(Vector3f.rotate(rotation, angle, Vector3f.Y_AXIS)));
	}

	@Override
	public final String toString() {
		return super.toString() + " position: " + position + " rotation: " + rotation + " scale: " + scale;
	}
}
