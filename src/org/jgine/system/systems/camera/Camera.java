package org.jgine.system.systems.camera;

import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.options.Options;
import org.jgine.render.RenderTarget;
import org.jgine.system.SystemObject;

/**
 * Represents a 3D Camera
 * 
 * @author Maximilian Paar
 */
public class Camera implements SystemObject {

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

	public Camera() {
		this(PERSPECTIVE_MODE, Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt(), DEFAULT_Z_NEAR,
				DEFAULT_Z_FAR, DEFAULT_FOV, Vector3f.Z_AXIS);
	}

	public Camera(Map<String, Object> data) {
		load(data);
	}

	public Camera(byte mode) {
		this(mode, Options.RESOLUTION_X.getInt(), Options.RESOLUTION_Y.getInt(), DEFAULT_Z_NEAR, DEFAULT_Z_FAR,
				DEFAULT_FOV, Vector3f.Z_AXIS);
	}

	public Camera(byte mode, int width, int height, float zNear, float zFar, float fov) {
		this(mode, width, height, zNear, zFar, fov, Vector3f.Z_AXIS);
	}

	public Camera(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		set(mode, width, height, zNear, zFar, fov, forward);
	}

	public final void set(int width, int height, float zNear, float zFar, float fov) {
		set(getMode(), width, height, zNear, zFar, fov, forward);
	}

	public final void set(int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		set(getMode(), width, height, zNear, zFar, fov, forward);
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov) {
		set(mode, width, height, zNear, zFar, fov, forward);
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
		this.fov = fov;
		this.forward = forward;
		this.up = Vector3f.UP;
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
		if (renderTarget != null)
			renderTarget.close();
		renderTarget = new RenderTarget(width, height);
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

	public final float getWidth() {
		return width;
	}

	public final float getHeight() {
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

	public RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public final void setMainCamera() {
		ServiceManager.set("camera", this);
	}

	public void load(Map<String, Object> data) {
		byte modeValue = PERSPECTIVE_MODE;
		Object mode = data.get("mode");
		if (mode != null) {
			if (mode instanceof Number)
				modeValue = ((Number) width).byteValue();
			else if (mode instanceof String) {
				if (((String) mode).equalsIgnoreCase("perspective"))
					modeValue = PERSPECTIVE_MODE;
				else if (((String) mode).equalsIgnoreCase("orthographic"))
					modeValue = ORTHOGRAPHIC_MODE;
			}
		}
		Object width = data.get("width");
		if (width != null && width instanceof Number)
			this.width = ((Number) width).intValue();
		else
			this.width = Options.RESOLUTION_X.getInt();
		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.height = ((Number) height).intValue();
		else
			this.height = Options.RESOLUTION_Y.getInt();
		Object zNear = data.get("zNear");
		if (zNear != null && zNear instanceof Number)
			this.zNear = ((Number) zNear).floatValue();
		else
			this.zNear = DEFAULT_Z_NEAR;
		Object zFar = data.get("zFar");
		if (zFar != null && zFar instanceof Number)
			this.zFar = ((Number) zFar).floatValue();
		else
			this.zFar = DEFAULT_Z_FAR;
		Object fov = data.get("fov");
		if (fov != null && fov instanceof Number)
			this.fov = ((Number) fov).floatValue();
		else
			this.fov = DEFAULT_FOV;

		Object forward = data.get("forward");
		if (forward != null) {
			float xValue = 0.0f;
			Object x = data.get("x");
			if (x != null && x instanceof Number)
				xValue = ((Number) x).floatValue();
			float yValue = 0.0f;
			Object y = data.get("y");
			if (y != null && y instanceof Number)
				yValue = ((Number) y).floatValue();
			float zValue = 0.0f;
			Object z = data.get("z");
			if (z != null && z instanceof Number)
				zValue = ((Number) z).floatValue();
			this.forward = new Vector3f(xValue, yValue, zValue);
		} else {
			this.forward = Vector3f.Z_AXIS;
		}

		this.up = Vector3f.UP;
		init(modeValue);
	}
}