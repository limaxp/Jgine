package org.jgine.utils.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.Scene;
import org.jgine.core.Transform;

/**
 * The default {@link SpacePartitioning} for a {@link Scene}. This basically
 * acts as if the {@link Scene} does not have any {@link SpacePartitioning} and
 * is the default {@link SpacePartitioning} a {@link Scene} uses after creation.
 * Checks will search the ENTIRE {@link Scene}.
 */
public class SceneSpacePartitioning implements SpacePartitioning {

	private final List<Transform> objects;

	public SceneSpacePartitioning() {
		objects = new UnorderedIdentityArrayList<Transform>();
	}

	@Override
	public void add(double x, double y, double z, Transform object) {
		objects.add(object);
	}

	@Override
	public void remove(double x, double y, double z, Transform object) {
		objects.remove(object);
	}

	@Override
	public void move(double xOld, double yOld, double zOld, double xNew, double yNew, double zNew, Transform object) {
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
	public Collection<Transform> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		List<Transform> result = new ArrayList<Transform>();
		forEach(xMin, yMin, zMin, xMax, yMax, zMax, result::add);
		return result;
	}

	@Override
	public Transform get(double x, double y, double z, Transform opt_default) {
		for (Transform transform : objects)
			if (transform.getX() == x && transform.getY() == y && transform.getZ() == z)
				return transform;
		return opt_default;
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
