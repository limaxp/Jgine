package jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.system.SystemObject;
import jgine.system.light.LightType;
import jgine.utils.Color;
import jgine.utils.Logger;
import jgine.utils.ObjectUtils;

public abstract class Light implements SystemObject {

	private int color = Color.WHITE;
	private float intensity = 1.0f;

	public abstract void setEntity(Entity entity);

	public abstract LightType<? extends Light> getType();

	@Override
	public void load(Map<String, Object> data) {
		color = ObjectUtils.toColor(data.get("color"), color);
		intensity = ObjectUtils.toFloat(data.get("intensity"), intensity);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("color", color);
		data.put("intensity", intensity);
	}

	@Override
	public void load(DataInput in) throws IOException {
		color = in.readInt();
		intensity = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(color);
		out.writeFloat(intensity);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getIntensity() {
		return intensity;
	}

	@Override
	public String toString() {
		return super.toString() + " [color: " + Color.toString(color) + " | intensity: " + intensity + "]";
	}

	@Override
	public int system() {
		return Engine.LIGHT;
	}

	@Override
	public Light clone() {
		try {
			return (Light) super.clone();
		} catch (CloneNotSupportedException e) {
			Logger.err("Light: Error on clone!", e);
			return null;
		}
	}
}
