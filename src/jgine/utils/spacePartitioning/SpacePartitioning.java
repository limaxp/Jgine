package jgine.utils.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A space partitioning is a data structure that organizes world space in some
 * manner to optimize near checks.
 */
public interface SpacePartitioning<T> {

	public void add(T object, double x, double y, double z);

	public void add(T object, double x, double y, double z, double r);

	public void remove(T object, double x, double y, double z);

	public void remove(T object, double x, double y, double z, double r);

	public void move(T object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew);

	public void move(T object, double xOld, double yOld, double zOld, double rOld, double xNew, double yNew,
			double zNew, double rNew);

	public default void forNear(double x, double y, double z, double r, Consumer<T> func) {
		forNear(x, y, z, r, r, r, func);
	}

	public default void forNear(double x, double y, double z, double rX, double rY, double rZ, Consumer<T> func) {
		forEach(x - rX, y - rY, z - rZ, x + rX, y + rY, z + rZ, func);
	}

	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Consumer<T> func);

	public default Set<T> getNear(double x, double y, double z, double r, Set<T> target) {
		return getNear(x, y, z, r, r, r, target);
	}

	public default Set<T> getNear(double x, double y, double z, double rX, double rY, double rZ, Set<T> target) {
		return get(x - rX, y - rY, z - rZ, x + rX, y + rY, z + rZ, target);
	}

	public Set<T> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Set<T> target);

	public void clear();

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;
}
