package jgine.utils.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jgine.utils.collection.list.UnorderedIdentityArrayList;

/**
 * Basic quad tree implementation. Quad trees divide space into 4 quads
 * recursively.
 */
public class QuadTree<T> implements SpacePartitioning<T> {

	private static final int MAX_DEPTH = 32;
	private static final int MAX_OBJECTS = 32;

	private Node<T> root;

	public QuadTree() {
	}

	public QuadTree(int xMin, int yMin, int xMax, int yMax) {
		this.root = new Node<>(0, xMin, yMin, xMax, yMax);
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
		this.root = new Node<>(0, in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeDouble(root.xMin);
		out.writeDouble(root.yMin);
		out.writeDouble(root.xMax);
		out.writeDouble(root.yMax);
	}

	public Node<T> getRoot() {
		return root;
	}

	public static class Node<T> {

		public final int depth;
		public final double xMin;
		public final double yMin;
		public final double xMax;
		public final double yMax;
		public final double xCenter;
		public final double yCenter;
		private final List<T> objects;
		private Node<T> childNW;
		private Node<T> childNO;
		private Node<T> childSW;
		private Node<T> childSO;

		public Node(int depth, double xMin, double yMin, double xMax, double yMax) {
			this.depth = depth;
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
			this.xCenter = (xMin + xMax) * 0.5;
			this.yCenter = (yMin + yMax) * 0.5;
			objects = new UnorderedIdentityArrayList<T>(MAX_OBJECTS);
		}

		public void add(double x, double y, T object) {
			if (objects.size() > MAX_OBJECTS && depth < MAX_DEPTH) {
				if (!hasChilds())
					createChilds();
				getChild(x, y).add(x, y, object);
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
				getChild(x, y).remove(x, y, object);
				if (childSW.isEmpty() && childSO.isEmpty() && childNW.isEmpty() && childNO.isEmpty())
					deleteChilds();
			}
		}

		public void move(double xOld, double yOld, double xNew, double yNew, T object) {
			int index = objects.indexOf(object);
			if (index == -1 && hasChilds()) {
				Node<T> oldNode = getChild(xOld, yOld);
				Node<T> newNode = getChild(xNew, yNew);
				if (oldNode == newNode)
					oldNode.move(xOld, yOld, xNew, yNew, object);
				else {
					oldNode.remove(xOld, yOld, object);
					newNode.add(xNew, yNew, object);
				}
			}
		}

		public void forNear(double xMin, double yMin, double xMax, double yMax, Consumer<T> func) {
			for (T object : objects)
				func.accept(object);

			if (hasChilds()) {
				if (yMin <= yCenter) {
					if (xMin <= xCenter)
						childSW.forNear(xMin, yMin, xMax, yMax, func);
					if (xMax > xCenter)
						childSO.forNear(xMin, yMin, xMax, yMax, func);
				}
				if (yMax > yCenter) {
					if (xMin <= xCenter)
						childNW.forNear(xMin, yMin, xMax, yMax, func);
					if (xMax > xCenter)
						childNO.forNear(xMin, yMin, xMax, yMax, func);
				}
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
			childSW = new Node<>(depth + 1, xMin, yMin, xCenter, yCenter);
			childSO = new Node<>(depth + 1, xCenter, yMin, xMax, yCenter);
			childNW = new Node<>(depth + 1, xMin, yCenter, xCenter, yMax);
			childNO = new Node<>(depth + 1, xCenter, yCenter, xMax, yMax);
		}

		protected void deleteChilds() {
			childSW = null;
			childSO = null;
			childNW = null;
			childNO = null;
		}

		protected Node<T> getChild(double x, double y) {
			if (y <= yCenter)
				if (x <= xCenter)
					return childSW;
				else
					return childSO;
			else if (x <= xCenter)
				return childNW;
			else
				return childNO;
		}

		protected boolean hasChilds() {
			return childSW != null;
		}
	}
}
