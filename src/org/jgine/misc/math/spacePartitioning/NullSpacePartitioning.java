package org.jgine.misc.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.jgine.core.Transform;

public class NullSpacePartitioning implements SpacePartitioning {

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
}