package org.jgine.system.systems.transform;

import java.util.function.BiConsumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.TaskManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.data.ListSystemScene;

public class TransformScene extends ListSystemScene<TransformSystem, Transform> {

	private final BiConsumer<Entity, Object> positionUpdate = (entity, pos) -> entity.getSystem(this)
			.setPositionNoUpdate((Vector3f) pos);

	private final BiConsumer<Entity, Object> scaleUpdate = (entity, scale) -> entity.getSystem(this).setScale(
			(Vector3f) scale);

	private final BiConsumer<Entity, Object> rotationUpdate = (entity, rotation) -> entity.getSystem(this).setRotation(
			(Vector3f) rotation);

	public TransformScene(TransformSystem system, Scene scene) {
		super(system, scene, Transform.class);
		UpdateManager.register(scene, "position", positionUpdate);
		UpdateManager.register(scene, "scale", scaleUpdate);
		UpdateManager.register(scene, "rotation", rotationUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, "position", positionUpdate);
		UpdateManager.unregister(scene, "scale", scaleUpdate);
		UpdateManager.unregister(scene, "rotation", rotationUpdate);
	}

	@Override
	public void initObject(Entity entity, Transform object) {
		object.setEntity(entity);
	}

	@Override
	public void update() {
		TaskManager.execute(size, this::update);
	}

	private void update(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			objects[index].calculateMatrix();
		}
	}

	@Override
	public void render() {}

	@Override
	public void parentUpdate(Entity parent, Transform object) {
		object.setHasChanged();
	}
}
