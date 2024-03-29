package org.jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.system.systems.light.LightType;
import org.jgine.system.systems.light.LightTypes;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.vector.Vector3f;

public class DirectionalLight extends Light {

	private Vector3f direction = Vector3f.UP;

	@Override
	public void setEntity(Entity entity) {
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object directionData = data.get("direction");
		if (directionData != null)
			direction = YamlHelper.toVector3f(directionData, Vector3f.Z_AXIS);
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		direction = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeFloat(direction.x);
		out.writeFloat(direction.y);
		out.writeFloat(direction.z);
	}

	@Override
	public LightType<DirectionalLight> getType() {
		return LightTypes.DIRECTIONAL;
	}
}
