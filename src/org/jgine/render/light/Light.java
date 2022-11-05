package org.jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.light.LightType;

public abstract class Light implements SystemObject {

	private int color;
	private float intensity;
	protected boolean hasChanged;

	public Light() {
		color = Color.WHITE;
		intensity = 1.0f;
		hasChanged = true;
	}

	public abstract LightType<? extends Light> getType();

	public void load(Map<String, Object> data) {
		color = YamlHelper.toColor(data.get("color"), Color.WHITE);
		intensity = YamlHelper.toFloat(data.get("intensity"), 1.0f);
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
		hasChanged = true;
	}

	public int getColor() {
		return color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
		hasChanged = true;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setChanged() {
		hasChanged = true;
	}

	public boolean checkChanged() {
		boolean result = hasChanged;
		hasChanged = false;
		return result;
	}
}
