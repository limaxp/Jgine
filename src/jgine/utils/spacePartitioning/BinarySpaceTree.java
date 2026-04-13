package jgine.utils.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jgine.utils.collection.list.UnorderedIdentityArrayList;

/**
 * Basic binary space tree implementation. Binary space trees divide space into
 * 2 regions recursively.
 */
public class BinarySpaceTree<T> implements SpacePartitioning<T> {

	private static final int MAX_DEPTH = 32;
	private static final int MAX_OBJECTS = 32;

	private Node<T> root;

	public BinarySpaceTree() {
	}

	public BinarySpaceTree(int yMin, int yMax) {
		this.root = new Node<>(0, yMin, yMax);
	}

	@Override
	public void add(T object, double x, double y, double z) {
		root.add(x, y, object);
	}

	@Override
	public void add(T object, double x, double y, double z, double r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(T object, double x, double y, double z) {
		root.remove(x, y, object);
	}

	@Override
	public void remove(T object, double x, double y, double z, double r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		root.move(xOld, yOld, xNew, yNew, object);
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double rOld, double xNew, double yNew,
			double zNew, double rNew) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<T> func) {
		root.forNear(xMin, yMin, xMax, yMax, func);
	}

	@Override
	public Set<T> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Set<T> target) {
		root.forNear(xMin, yMin, xMax, yMax, target::add);
		return target;
	}

	@Override
	public void clear() {
		root.clear();
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.root = new Node<>(0, in.readDouble(), in.readDouble());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeDouble(root.yMin);
		out.writeDouble(root.yMax);
	}

	public Node<T> getRoot() {
		return root;
	}

	public static class Node<T> {

		public final int depth;
		public final double yMin;
		public final double yMax;
		public final double yCenter;
		private final List<T> objects;
		private Node<T> childA;
		private Node<T> childB;

		public Node(int depth, double yMin, double yMax) {
			this.depth = depth;
			this.yMin = yMin;
			this.yMax = yMax;
			this.yCenter = (yMin + yMax) * 0.5;
			objects = new UnorderedIdentityArrayList<T>(MAX_OBJECTS);
		}

		public void add(double x, double y, T object) {
			if (objects.size() > MAX_OBJECTS && depth < MAX_DEPTH) {
				if (!hasChilds())
					createChilds();
				getChild(y).add(x, y, object);
				return;
			}
			objects.add(object);
		}

		public void remove(double x, double y, T object) {
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

		public void move(double xOld, double yOld, double xNew, double yNew, T object) {
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

		public void forNear(double xMin, double yMin, double xMax, double yMax, Consumer<T> func) {
			for (T object : objects)
				func.accept(object);

			if (hasChilds()) {
				if (yMin <= yCenter)
					childA.forNear(xMin, yMin, xMax, yMax, func);
				if (yMax > yCenter)
					childB.forNear(xMin, yMin, xMax, yMax, func);
			}
		}

		public boolean isEmpty() {
			return objects.isEmpty();
		}

		public void clear() {
			objects.clear();
			deleteChilds();
		}

		protected void createChilds() {
			childA = new Node<>(depth + 1, yMin, yCenter);
			childB = new Node<>(depth + 1, yCenter, yMax);
		}

		protected void deleteChilds() {
			childA = null;
			childB = null;
		}

		protected Node<T> getChild(double y) {
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
