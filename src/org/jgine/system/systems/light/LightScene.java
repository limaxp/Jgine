package org.jgine.system.systems.light;

import java.util.List;
import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.Light;
import org.jgine.render.light.PointLight;
import org.jgine.system.data.ListSystemScene;

public class LightScene extends ListSystemScene<LightSystem, Light> {

	private final BiConsumer<Entity, Object> positionUpdate = (entity, pos) -> {
		Light light = entity.getSystem(this);
		if (light instanceof PointLight)
			((PointLight) light).setPosition((Vector3f) pos);
	};

	public LightScene(LightSystem system, Scene scene) {
		super(system, scene, Light.class);
		UpdateManager.register(scene, "position", positionUpdate);
//		UpdateManager.register(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, "position", positionUpdate);
//		UpdateManager.unregister(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void initObject(Entity entity, Light object) {
	}

	@Override
	public Light addObject(Entity entity, Light object) {
		if (object instanceof PointLight)
			Renderer.PHONG_SHADER.addPointLight((PointLight) object);
		if (object instanceof DirectionalLight)
			;
		return super.addObject(entity, object);
	}

	@Override
	@Nullable
	public Light removeObject(Light object) {
		if (object instanceof PointLight)
			Renderer.PHONG_SHADER.removePointLight((PointLight) object);
		if (object instanceof DirectionalLight)
			;
		return super.removeObject(object);
	}

	public List<PointLight> getPointLights() {
		return Renderer.PHONG_SHADER.getPointLights();
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
	}
}
