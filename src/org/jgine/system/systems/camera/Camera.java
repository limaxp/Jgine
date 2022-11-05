package org.jgine.system.systems.camera;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.RenderTarget;
import org.jgine.system.SystemObject;

/**
 * Represents a 3D Camera
 * 
 * @author Maximilian Paar
 */
public class Camera implements SystemObject, Cloneable {

	public static final byte PERSPECTIVE_MODE = 1;
	public static final byte ORTHOGRAPHIC_MODE = 2;

	public static final float DEFAULT_Z_NEAR = 0.1f;
	public static final float DEFAULT_Z_FAR = 1000f;
	public static final float DEFAULT_FOV = 45f;

	protected Transform transform;
	private int width;
	private int height;
	private float zNear;
	private float zFar;
	private float fov;
	private Vector3f forward;
	private Vector3f up;
	private Matrix perspectiveMatrix;
	private Matrix orthographicMatrix;
	private Matrix usedMatrix;
	private RenderTarget renderTarget;

	public static Camera create() {
		return create(PERSPECTIVE_MODE, Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt(), DEFAULT_Z_NEAR,
				DEFAULT_Z_FAR, DEFAULT_FOV, Vector3f.Z_AXIS);
	}

	public static Camera create(byte mode) {
		return create(mode, Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt(), DEFAULT_Z_NEAR, DEFAULT_Z_FAR,
				DEFAULT_FOV, Vector3f.Z_AXIS);
	}

	public static Camera create(byte mode, int width, int height, float zNear, float zFar, float fov) {
		return create(mode, width, height, zNear, zFar, fov, Vector3f.Z_AXIS);
	}

	public static Camera create(byte mode, int width, int height, float zNear, float zFar, float fov,
			Vector3f forward) {
		return create(mode, width, height, zNear, zFar, fov, Vector3f.Z_AXIS, Vector3f.UP);
	}

	public static Camera create(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward,
			Vector3f up) {
		Camera camera = new Camera();
		camera.set(mode, width, height, zNear, zFar, fov, forward, up);
		return camera;
	}

	protected Camera() {
	}

	public final void set(int width, int height, float zNear, float zFar, float fov) {
		set(getMode(), width, height, zNear, zFar, fov, forward, up);
	}

	public final void set(int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		set(getMode(), width, height, zNear, zFar, fov, forward, up);
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov) {
		set(mode, width, height, zNear, zFar, fov, forward, up);
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		set(mode, width, height, zNear, zFar, fov, forward, up);
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward,
			Vector3f up) {
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
		this.fov = fov;
		this.forward = forward;
		this.up = up;
		init(mode);
	}

	private final void init(byte mode) {
		perspectiveMatrix = Matrix.asPerspective(fov, width, height, zNear, zFar);
		int widthHalf = (int) (width * 0.5);
		int heightHalf = (int) (width * 0.5);
		orthographicMatrix = Matrix.asOrthographic(-widthHalf, widthHalf, -heightHalf, heightHalf, zNear, zFar);
		if (mode == PERSPECTIVE_MODE)
			setPerspective();
		else if (mode == ORTHOGRAPHIC_MODE)
			setOrthographic();
	}

	public final void setPerspective() {
		usedMatrix = perspectiveMatrix;
	}

	public final boolean isPerspective() {
		return usedMatrix == perspectiveMatrix;
	}

	public final void setOrthographic() {
		usedMatrix = orthographicMatrix;
	}

	public final boolean isOrthographic() {
		return usedMatrix == orthographicMatrix;
	}

	public final byte getMode() {
		if (isPerspective())
			return PERSPECTIVE_MODE;
		if (isOrthographic())
			return ORTHOGRAPHIC_MODE;
		return 0;
	}

	public final void rotateX(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, hAxis));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
	}

	public final void rotateY(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, Vector3f.Y_AXIS));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
	}

	public final Matrix getMatrix() {
		Vector3f pos = transform.getPosition();
		Matrix matrix = new Matrix();
		matrix.setPosition(-pos.x, -pos.y, -pos.z);
		matrix.mult(Matrix.asCameraRotation(forward, up));
		matrix.mult(usedMatrix);
		return matrix;

		// return new Matrix()
		// .perspective((float) Math.toRadians(perspective.fov),
		// perspective.getAspectRatio(), perspective.zNear,
		// perspective.zFar)
		// .lookAt(pos, forward, up);
	}

	public final Matrix getPerspectiveMatrix() {
		return perspectiveMatrix;
	}

	public final Matrix getOrthographicMatrix() {
		return orthographicMatrix;
	}

	public final Transform getTransform() {
		return transform;
	}

	public final Vector3f getForwardDirection() {
		return forward;
	}

	public final Vector3f getUpDirection() {
		return up;
	}

	public final Vector3f getLeftDirection() {
		return Vector3f.cross(forward, up);
	}

	public final Vector3f getRightDirection() {
		return Vector3f.cross(up, forward);
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final int getLeft() {
		return -(int) (width * 0.5);
	}

	public final int getRight() {
		return (int) (width * 0.5);
	}

	public final int getBottom() {
		return -(int) (height * 0.5);
	}

	public final int getTop() {
		return (int) (height * 0.5);
	}

	public final float getZNear() {
		return zNear;
	}

	public final float getZFar() {
		return zFar;
	}

	public final float getFov() {
		return fov;
	}

	public final float getAspectRatio() {
		return (float) width / height;
	}

	public final void setRenderTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}

	public final RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public final void setMainCamera() {
		ServiceManager.set("camera", this);
	}

	public void load(Map<String, Object> data) {
		byte modeValue = PERSPECTIVE_MODE;
		Object mode = data.get("mode");
		if (mode instanceof Number)
			modeValue = ((Number) width).byteValue();
		else if (mode instanceof String) {
			if (((String) mode).equalsIgnoreCase("perspective"))
				modeValue = PERSPECTIVE_MODE;
			else if (((String) mode).equalsIgnoreCase("orthographic"))
				modeValue = ORTHOGRAPHIC_MODE;
		}
		width = YamlHelper.toInt(data.get("width"), Options.RESOLUTION_X.getInt());
		height = YamlHelper.toInt(data.get("height"), Options.RESOLUTION_Y.getInt());
		zNear = YamlHelper.toFloat(data.get("zNear"), DEFAULT_Z_NEAR);
		zFar = YamlHelper.toFloat(data.get("zFar"), DEFAULT_Z_FAR);
		fov = YamlHelper.toFloat(data.get("fov"), DEFAULT_FOV);
		forward = YamlHelper.toVector3f(data.get("forward"), Vector3f.Z_AXIS);
		up = YamlHelper.toVector3f(data.get("up"), Vector3f.UP);
		init(modeValue);
	}

	public void load(DataInput in) throws IOException {
		byte modeValue = in.readByte();
		width = in.readInt();
		height = in.readInt();
		zNear = in.readFloat();
		zFar = in.readFloat();
		fov = in.readFloat();
		forward = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		up = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		init(modeValue);
	}

	public void save(DataOutput out) throws IOException {
		out.write(getMode());
		out.writeInt(width);
		out.writeInt(height);
		out.writeFloat(zNear);
		out.writeFloat(zFar);
		out.writeFloat(fov);
		out.writeFloat(forward.x);
		out.writeFloat(forward.y);
		out.writeFloat(forward.z);
		out.writeFloat(up.x);
		out.writeFloat(up.y);
		out.writeFloat(up.z);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public Camera clone() {
		try {
			return (Camera) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}