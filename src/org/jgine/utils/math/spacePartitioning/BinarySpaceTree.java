package org.jgine.utils.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.entity.Entity;

/**
 * Basic binary space tree implementation. Binary space trees divide space into
 * 2 regions recursively.
 * <p>
 * <strong>NOT FULLY IMPLEMENTED YET!</strong>
 */
public class BinarySpaceTree implements SpacePartitioning {

	private static final int MAX_DEPTH = 32;
	private static final int MAX_OBJECTS = 32;

	private Node root;

	public BinarySpaceTree() {
	}

	public BinarySpaceTree(double yMin, double yMax) {
		this.root = new Node(0, yMin, yMax);
	}

	@Override
	public void add(Entity object) {
		root.add(object.transform.getX(), object.transform.getY(), object);
	}

	@Override
	public void remove(Entity object) {
		root.remove(object.transform.getX(), object.transform.getY(), object);
	}

	@Override
	public void move(Entity object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		root.move(xOld, yOld, xNew, yNew, object);
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Entity> func) {
		root.forEach(xMin, yMin, xMax, yMax, func);
	}

	@Override
	public Collection<Entity> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		List<Entity> result = new ArrayList<Entity>();
		root.forEach(xMin, yMin, xMax, yMax, result::add);
		return result;
	}

	@Override
	public Entity get(double x, double y, double z, Entity opt_default) {
		return root.get(x, y, opt_default);
	}

	@Override
	public void clear() {
		root.clear();
	}

	@Override
	public SpacePartitioningType<?> getType() {
		return SpacePartitioningTypes.BINARY_SPACE_TREE;
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.root = new Node(0, in.readDouble(), in.readDouble());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeDouble(root.yMin);
		out.writeDouble(root.yMax);
	}

	public Node getRoot() {
		return root;
	}

	public static class Node {

		public final int depth;
		public final double yMin;
		public final double yMax;
		public final double yCenter;
		private final List<Entity> objects;
		private Node childA;
		private Node childB;

		public Node(int depth, double yMin, double yMax) {
			this.depth = depth;
			this.yMin = yMin;
			this.yMax = yMax;
			this.yCenter = (yMin + yMax) * 0.5;
			objects = new UnorderedIdentityArrayList<Entity>(MAX_OBJECTS);
		}

		public void add(double x, double y, Entity object) {
			if (objects.size() > MAX_OBJECTS && depth < MAX_DEPTH) {
				if (!hasChilds())
					createChilds();
				getChild(y).add(x, y, object);
				return;
			}
			objects.add(object);
		}

		public void remove(double x, double y, Entity object) {
			int index = objects.indexOf(object);
			if (index != -1) {
				objects.remove(index);
				return;
			}
			if (hasChilds()) {
				getChild(y).remove(x, y, object);
				if (childA.isEmpty() && childB.isEmpty())
					deleteChilds();
			}
		}

		public void move(double xOld, double yOld, double xNew, double yNew, Entity object) {
			int index = objects.indexOf(object);
			if (index == -1 && hasChilds()) {
				if (yNew <= yCenter) {
					if (yOld <= yCenter)
						childA.move(xOld, yOld, xNew, yNew, object);
					else {
						childB.remove(xOld, yOld, object);
						childA.add(xNew, yNew, object);
					}
				} else {
					if (yOld <= yCenter) {
						childA.remove(xOld, yOld, object);
						childB.add(xNew, yNew, object);
					} else
						childB.move(xOld, yOld, xNew, yNew, object);
				}
			}
		}

		public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Entity> func) {
			for (Entity object : objects)
				if (object.transform.getX() >= xMin && object.transform.getY() >= yMin && object.transform.getX() < xMax
						&& object.transform.getY() < yMax)
					func.accept(object);

			if (hasChilds()) {
				if (yMin <= yCenter)
					childA.forEach(xMin, yMin, xMax, yMax, func);
				if (yMax > yCenter)
					childB.forEach(xMin, yMin, xMax, yMax, func);
			}
		}

		public Entity get(double x, double y, Entity opt_default) {
			for (Entity object : objects)
				if (object.transform.getX() == x && object.transform.getY() == y)
					return object;

			if (hasChilds())
				return getChild(y).get(x, y, opt_default);
			return opt_default;
		}

		public boolean isEmpty() {
			return objects.isEmpty();
		}

		public void clear() {
			objects.clear();
			deleteChilds();
		}

		protected void createChilds() {
			childA = new Node(depth + 1, yMin, yCenter);
			childB = new Node(depth + 1, yCenter, yMax);
		}

		protected void deleteChilds() {
			childA = null;
			childB = null;
		}

		protected Node getChild(double y) {
			if (y <= yCenter)
				return childA;
			else
				return childB;
		}

		protected boolean hasChilds() {
			return childA != null;
		}
	}
}
