package org.jgine.misc.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.jgine.core.Transform;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;

public class SceneSpacePartitioning implements SpacePartitioning {

	private final List<Transform> objects;

	public SceneSpacePartitioning() {
		objects = new UnorderedIdentityArrayList<Transform>();
	}

	@Override
	public void add(double x, double y, Transform object) {
		objects.add(object);
	}

	@Override
	public void remove(double x, double y, Transform object) {
		objects.remove(object);
	}

	@Override
	public void move(double xOld, double yOld, double xNew, double yNew, Transform object) {
	}

	@Override
	public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Transform> func) {
		for (Transform transform : objects)
			if (transform.getX() >= xMin && transform.getY() >= yMin && transform.getX() < xMax
					&& transform.getY() < yMax)
				func.accept(transform);
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Transform> func) {
		for (Transform transform : objects)
			if (transform.getX() >= xMin && transform.getY() >= yMin && transform.getZ() >= zMin
					&& transform.getX() < xMax && transform.getY() < yMax && transform.getZ() < zMax)
				func.accept(transform);
	}

	@Override
	public Transform get(double x, double y, Transform opt_default) {
		for (Transform transform : objects)
			if (transform.getX() == x && transform.getY() == y)
				return transform;
		return opt_default;
	}

	@Override
	public Transform get(double x, double y, double z, Transform opt_default) {
		for (Transform transform : objects)
			if (transform.getX() == x && transform.getY() == y && transform.getZ() == z)
				return transform;
		return opt_default;
	}

	@Override
	public boolean contains(double x, double y, Transform object) {
		for (Transform transform : objects)
			if (transform == object)
				return true;
		return false;
	}

	@Override
	public boolean contains(double x, double y, double z, Transform object) {
		for (Transform transform : objects)
			if (transform == object)
				return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		return objects.isEmpty();
	}

	@Override
	public int size() {
		return objects.size();
	}

	@Override
	public void clear() {
	}

	@Override
	public SpacePartitioningType<?> getType() {
		return SpacePartitioningTypes.SCENE;
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}
}
