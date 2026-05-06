package jgine.system.transform;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.core.Engine.UpdateTask;
import jgine.system.UpdateManager;
import jgine.utils.math.Matrix;
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
			Transform object = get(i);
			if (!object.isDirty())
				continue;
			update(object);
		}
		update.finish(id);
	}

	private void update(Transform object) {
		update(object, null);
	}

	private void update(Transform object, Matrix parent) {
		float oldX = object.getX();
		float oldY = object.getY();
		float oldZ = object.getZ();
		float oldScaleX = object.getScaleX();
		float oldScaleY = object.getScaleY();
		float oldScaleZ = object.getScaleZ();

		Matrix matrix = object.calculateMatrix();
		if (parent != null)
			matrix.mult(parent);
		float x = object.getX();
		float y = object.getY();
		float z = object.getZ();

		Entity entity = object.getEntity();
		spacePartitioning.move(entity, oldX, oldY, oldZ, x, y, z);
		UpdateManager.getTransformPosition().accept(entity, x, y, z);
		UpdateManager.getTransformScale().accept(entity, 1.0f + object.getScaleX() - oldScaleX,
				1.0f + object.getScaleY() - oldScaleY, 1.0f + object.getScaleZ() - oldScaleZ);
		for (Transform child : object.getChilds())
			update(child, matrix);
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
		if (object.hasParent())
			return;
		saveSubData(object, out);
	}

	protected void saveSubData(Transform object, DataOutput out) throws IOException {
		object.save(out);
		List<Transform> childs = object.getChilds();
		int childSize = childs.size();
		out.writeInt(childSize);
		for (int j = 0; j < childSize; j++)
			saveSubData(childs.get(j), out);
	}

	@Override
	protected Transform loadData(DataInput in) throws IOException {
		// TODO childs need to set index to set in data array!
		Transform object = new Transform();
		object.load(in);
		int childSize = in.readInt();
		for (int i = 0; i < childSize; i++)
			object.addChild(loadData(in));
		return object;
	}

	public SpatialHashing2d<Entity> getSpacePartitioning() {
		return spacePartitioning;
	}
}
