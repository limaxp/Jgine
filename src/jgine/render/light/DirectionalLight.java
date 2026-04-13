package jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import jgine.core.Entity;
import jgine.system.light.LightType;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector3f;

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
		direction = ObjectUtils.toVector3f(data.get("direction"), direction);
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		data.put("direction", Arrays.asList(direction.x, direction.y, direction.z));
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
		return LightType.DIRECTIONAL;
	}
}
