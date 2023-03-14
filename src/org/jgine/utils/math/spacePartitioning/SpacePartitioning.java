package org.jgine.utils.math.spacePartitioning;

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
		public void add(Transform object) {
		}

		@Override
		public void remove(Transform object) {
		}

		@Override
		public void move(Transform object, double xOld, double yOld, double zOld, double xNew, double yNew,
				double zNew) {
		}

		@Override
		public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
				Consumer<Transform> func) {
		}

		@Override
		public Collection<Transform> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
			return new ArrayList<Transform>();
		}

		@Override
		public Transform get(double x, double y, double z, Transform opt_default) {
			return null;
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

	public void add(Transform object);

	public void remove(Transform object);

	public void move(Transform object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew);

	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Transform> func);

	public Collection<Transform> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax);

	public Transform get(double x, double y, double z, Transform opt_default);

	public void clear();

	public SpacePartitioningType<?> getType();

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;
}
