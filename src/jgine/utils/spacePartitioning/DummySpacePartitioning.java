package jgine.utils.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This basically acts as if the there is no {@link SpacePartitioning}. Checks
 * will search all values.
 */
public class DummySpacePartitioning<T> implements SpacePartitioning<T> {

	private final Set<T> objects;

	public DummySpacePartitioning() {
		this(1024);
	}

	public DummySpacePartitioning(int initalSize) {
		objects = new HashSet<>(initalSize);
	}

	@Override
	public void add(T object, double x, double y, double z) {
		objects.add(object);
	}

	@Override
	public void add(T object, double x, double y, double z, double r) {
		objects.add(object);
	}

	@Override
	public void remove(T object, double x, double y, double z) {
		objects.remove(object);
	}

	@Override
	public void remove(T object, double x, double y, double z, double r) {
		objects.remove(object);
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double rOld, double xNew, double yNew,
			double zNew, double rNew) {
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<T> func) {
		objects.forEach(func);
	}

	@Override
	public Set<T> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Set<T> target) {
		return objects;
	}

	@Override
	public void clear() {
		objects.clear();
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}
}
