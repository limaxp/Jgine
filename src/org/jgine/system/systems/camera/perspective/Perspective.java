package org.jgine.system.systems.camera.perspective;

import java.util.Map;

public class Perspective {

	public static final float Z_NEAR = 0.1f;
	public static final float Z_FAR = 1000f;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final float FOV = 45f;
	public static final Perspective DEFAULT = new Perspective(FOV, WIDTH, HEIGHT, Z_NEAR, Z_FAR);

	public final float zNear;
	public final float zFar;
	public final int width;
	public final int height;
	public final float fov;

	public Perspective(float fov, int width, int height, float zNear, float zFar) {
		this.fov = fov;
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public Perspective(Map<String, Object> data) {
		Object width = data.get("width");
		if (width != null && width instanceof Number)
			this.width = ((Number) width).intValue();
		else
			this.width = WIDTH;
		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.height = ((Number) height).intValue();
		else
			this.height = HEIGHT;
		Object fov = data.get("fov");
		if (fov != null && fov instanceof Number)
			this.fov = ((Number) fov).floatValue();
		else
			this.fov = FOV;
		Object zNear = data.get("zNear");
		if (zNear != null && zNear instanceof Number)
			this.zNear = ((Number) zNear).floatValue();
		else
			this.zNear = Z_NEAR;
		Object zFar = data.get("zFar");
		if (zFar != null && zFar instanceof Number)
			this.zFar = ((Number) zFar).floatValue();
		else
			this.zFar = Z_FAR;
	}

	public final float getFov() {
		return fov;
	}

	public final float getHeight() {
		return height;
	}

	public final float getWidth() {
		return width;
	}

	public final float getZFar() {
		return zFar;
	}

	public final float getZNear() {
		return zNear;
	}

	public final float getAspectRatio() {
		return (float) width / height;
	}
}
