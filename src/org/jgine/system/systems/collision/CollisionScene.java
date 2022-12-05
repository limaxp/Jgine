package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	static {
		UpdateManager.addTransformScale((entity, scale) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.scale(scale);
		});
	}

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Collider object) {
		object.scale(entity.transform.getScale());
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render() {
		if (!system.showHitBox())
			return;
		Renderer.setShader(Renderer.BASIC_SHADER);
		Renderer.enableDepthTest();
		for (int i = 0; i < size; i++)
			objects[i].render(entities[i].transform.getPosition());
		Renderer.disableDepthTest();
	}

	@Override
	public Collider load(DataInput in) throws IOException {
		Collider object = ColliderTypes.get(in.readInt()).get();
		object.load(in);
		return object;
	}

	@Override
	public void save(Collider object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
		object.save(out);
	}
}
