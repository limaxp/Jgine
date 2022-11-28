package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.BiConsumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	private final BiConsumer<Entity, Object> scaleUpdate = (entity, scale) -> {
		Collider collider = entity.getSystem(this);
		if (collider != null)
			collider.scale((Vector3f) scale);
	};

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class);
		UpdateManager.register(scene, UpdateManager.TRANSFORM_SCALE_IDENTIFIER, scaleUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, UpdateManager.TRANSFORM_SCALE_IDENTIFIER, scaleUpdate);
	}

	@Override
	public void initObject(Entity entity, Collider object) {
		object.scale(entity.transform.getScale());
	}

	@Override
	public void update() {
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
