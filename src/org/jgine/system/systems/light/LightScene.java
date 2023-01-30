package org.jgine.system.systems.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.Light;
import org.jgine.render.light.PointLight;
import org.jgine.system.data.ListSystemScene;

public class LightScene extends ListSystemScene<LightSystem, Light> {

	public LightScene(LightSystem system, Scene scene) {
		super(system, scene, Light.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Light object) {
		object.setEntity(entity);
	}

	@Override
	public Light addObject(Entity entity, Light object) {
		if (object instanceof PointLight)
			Renderer.PHONG_SHADER.getPointLights().add((PointLight) object);
		if (object instanceof DirectionalLight)
			;
		return super.addObject(entity, object);
	}

	@Override
	@Nullable
	public Light removeObject(Light object) {
		if (object instanceof PointLight)
			Renderer.PHONG_SHADER.getPointLights().remove((PointLight) object);
		if (object instanceof DirectionalLight)
			;
		return super.removeObject(object);
	}

	public List<PointLight> getPointLights() {
		return Renderer.PHONG_SHADER.getPointLights();
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render() {
	}

	@Override
	public Light load(DataInput in) throws IOException {
		Light object = LightTypes.get(in.readInt()).get();
		object.load(in);
		return object;
	}

	@Override
	public void save(Light object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
		object.save(out);
	}
}
