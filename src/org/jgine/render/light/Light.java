package org.jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.light.LightType;
import org.jgine.utils.loader.YamlHelper;

import maxLibs.utils.Color;

public abstract class Light implements SystemObject {

	private int color;
	private float intensity;

	public Light() {
		color = Color.WHITE;
		intensity = 1.0f;
	}

	public abstract void setEntity(Entity entity);

	public abstract LightType<? extends Light> getType();

	public void load(Map<String, Object> data) {
		Object colorData = data.get("color");
		if (colorData != null)
			color = YamlHelper.toColor(colorData, Color.WHITE);
		Object intensityData = data.get("intensity");
		if (intensityData != null)
			intensity = YamlHelper.toFloat(intensityData, 1.0f);
	}

	public void load(DataInput in) throws IOException {
		color = in.readInt();
		intensity = in.readFloat();
	}

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
}
