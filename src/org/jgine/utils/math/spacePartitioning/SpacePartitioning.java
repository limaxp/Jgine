package org.jgine.utils.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.jgine.core.entity.Entity;

/**
 * Base interface for space partitioning. A space partitioning is a data
 * structure that organizes world space in some manner to optimize location and
 * near {@link Entity} checks.
 */
public interface SpacePartitioning {

	public static final SpacePartitioning NULL = new SpacePartitioning() {
		@Override
		public void add(Entity object) {
		}

		@Override
		public void remove(Entity object) {
		}

		@Override
		public void move(Entity object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		}

		@Override
		public void forNear(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
				Consumer<Entity> func) {
		}

		@Override
		public Collection<Entity> getNear(double xMin, double yMin, double zMin, double xMax, double yMax,
				double zMax) {
			return new ArrayList<Entity>();
		}

		@Override
		public Entity get(double x, double y, double z, Entity opt_default) {
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

	public void add(Entity object);

	public void remove(Entity object);

	public void move(Entity object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew);

	public void forNear(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Entity> func);

	public Collection<Entity> getNear(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax);

	public Entity get(double x, double y, double z, Entity opt_default);

	public void clear();

	public SpacePartitioningType<?> getType();

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;
}
