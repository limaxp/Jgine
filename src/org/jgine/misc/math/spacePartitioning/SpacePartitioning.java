package org.jgine.misc.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;

/**
 * Base interface for space partitioning. A space partitioning is a data
 * structure that organizes world space in some manner to optimize location and
 * near {@link Entity} checks.
 */
public interface SpacePartitioning {

	public static final SpacePartitioning NULL = new SpacePartitioning() {
		@Override
		public void add(double x, double y, Transform object) {
		}

		@Override
		public void remove(double x, double y, Transform object) {
		}

		@Override
		public void move(double xOld, double yOld, double xNew, double yNew, Transform object) {
		}

		@Override
		public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Transform> func) {
		}

		@Override
		public Collection<Transform> get(double xMin, double yMin, double xMax, double yMax) {
			return new ArrayList<Transform>();
		}

		@Override
		public Transform get(double x, double y, Transform opt_default) {
			return null;
		}

		@Override
		public boolean contains(double x, double y, Transform object) {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public void clear() {
		}

		@Override
		public SpacePartitioningType<?> getType() {
			return null;
		}

		@Override
		public void load(DataInput in) throws IOException {
		}

		@Override
		public void save(DataOutput out) throws IOException {
		}
	};

	public void add(double x, double y, Transform object);

	public default void add(double x, double y, double z, Transform object) {
		add(x, y, object);
	}

	public void remove(double x, double y, Transform object);

	public default void remove(double x, double y, double z, Transform object) {
		remove(x, y, object);
	}

	public void move(double xOld, double yOld, double xNew, double yNew, Transform object);

	public default void move(double xOld, double yOld, double zOld, double xNew, double yNew, double zNew,
			Transform object) {
		move(xOld, yOld, xNew, yNew, object);
	}

	public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Transform> func);

	public default void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Transform> func) {
		forEach(xMin, yMin, xMax, yMax, func);
	}

	public Collection<Transform> get(double xMin, double yMin, double xMax, double yMax);

	public default Collection<Transform> get(double xMin, double yMin, double zMin, double xMax, double yMax,
			double zMax) {
		return get(xMin, yMin, xMax, yMax);
	}

	public Transform get(double x, double y, Transform opt_default);

	public default Transform get(double x, double y, double z, Transform opt_default) {
		return get(x, y, opt_default);
	}

	public boolean contains(double x, double y, Transform object);

	public default boolean contains(double x, double y, double z, Transform object) {
		return contains(x, y, object);
	}

	public boolean isEmpty();

	public int size();

	public void clear();

	public SpacePartitioningType<?> getType();

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;
}
