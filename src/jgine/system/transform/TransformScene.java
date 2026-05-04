package jgine.system.transform;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.core.Engine.UpdateTask;
import jgine.system.UpdateManager;
import jgine.utils.spacePartitioning.SpatialHashing2d;
import jgine.system.ObjectSystemScene;

public class TransformScene extends ObjectSystemScene<TransformSystem, Transform> {

	public SpatialHashing2d<Entity> spacePartitioning;

	public TransformScene(TransformSystem system, Scene scene) {
		super(system, scene, Transform.class, 100000);
		spacePartitioning = new SpatialHashing2d<Entity>(100000, 100);
	}

	@Override
	public void free() {
		forEach(Transform::free);
	}

	@Override
	public void onInit(Entity entity, Transform object) {
		entity.initTransform(object);
		object.setEntity(entity);
	}

	@Override
	protected void onAdd(Entity entity, Transform object) {
		spacePartitioning.add(entity, object.getX(), object.getY(), object.getZ());
	}

	@Override
	protected void onRemove(Entity entity, Transform object) {
		spacePartitioning.remove(entity, object.getX(), object.getY(), object.getZ());
		object.free();
	}

	@Override
	public void update(UpdateTask update) {
		int size = size();
		for (int i = 0; i < size; i++) {
			Transform transform = get(i);
			if (!transform.isDirty())
				continue;

			float oldX = transform.getX();
			float oldY = transform.getY();
			float oldZ = transform.getZ();
			float oldScaleX = transform.getScaleX();
			float oldScaleY = transform.getScaleY();
			float oldScaleZ = transform.getScaleZ();

			transform.calculateMatrix();
			float newX = transform.getX();
			float newY = transform.getY();
			float newZ = transform.getZ();
			Entity entity = getEntity(i);
			spacePartitioning.move(entity, oldX, oldY, oldZ, newX, newY, newZ);
			UpdateManager.getTransformPosition().accept(entity, newX, newY, newZ);
			UpdateManager.getTransformScale().accept(entity, 1.0f + transform.getScaleX() - oldScaleX,
					1.0f + transform.getScaleY() - oldScaleY, 1.0f + transform.getScaleZ() - oldScaleZ);
		}
		update.finish(id);
	}

	@Override
	public Entity getEntity(int index) {
		return get(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return get(index);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		spacePartitioning.save(out);
		super.save(out);
	}

	@Override
	public void load(DataInput in) throws IOException {
		spacePartitioning.load(in);
		super.load(in);
	}

	@Override
	protected void saveData(Transform object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected Transform loadData(DataInput in) throws IOException {
		Transform object = new Transform();
		object.load(in);
		return object;
	}

	public SpatialHashing2d<Entity> getSpacePartitioning() {
		return spacePartitioning;
	}
}
