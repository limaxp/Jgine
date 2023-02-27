package org.jgine.misc.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.jgine.core.Transform;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;

public class QuadTree implements SpacePartitioning {

	private static final int MAX_DEPTH = 32;
	private static final int MAX_OBJECTS = 32;

	private Node root;
	private int size;

	public QuadTree() {
	}

	public QuadTree(double xMin, double yMin, double xMax, double yMax) {
		this.root = new Node(0, xMin, yMin, xMax, yMax);
	}

	@Override
	public void add(double x, double y, Transform object) {
		root.add(x, y, object);
		size++;
	}

	@Override
	public void remove(double x, double y, Transform object) {
		root.remove(x, y, object);
		size--;
	}

	@Override
	public void move(double xOld, double yOld, double xNew, double yNew, Transform object) {
		root.move(xOld, yOld, xNew, yNew, object);
	}

	@Override
	public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Transform> func) {
		root.forEach(xMin, yMin, xMax, yMax, func);
	}

	@Override
	public Collection<Transform> get(double xMin, double yMin, double xMax, double yMax) {
		List<Transform> result = new ArrayList<Transform>();
		forEach(xMin, yMin, xMax, yMax, result::add);
		return result;
	}

	@Override
	public Transform get(double x, double y, Transform opt_default) {
		return root.get(x, y, opt_default);
	}

	@Override
	public boolean contains(double x, double y, Transform object) {
		return root.contains(x, y, object);
	}

	@Override
	public boolean isEmpty() {
		return root.isEmpty();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		root.clear();
	}

	@Override
	public SpacePartitioningType<?> getType() {
		return SpacePartitioningTypes.QUAD_TREE;
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.root = new Node(0, in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeDouble(root.xMin);
		out.writeDouble(root.yMin);
		out.writeDouble(root.xMax);
		out.writeDouble(root.yMax);
	}

	public Node getRoot() {
		return root;
	}

	public static class Node {

		public final int depth;
		public final double xMin;
		public final double yMin;
		public final double xMax;
		public final double yMax;
		public final double xCenter;
		public final double yCenter;
		private final List<Transform> objects;
		private Node childNW;
		private Node childNO;
		private Node childSW;
		private Node childSO;

		public Node(int depth, double xMin, double yMin, double xMax, double yMax) {
			this.depth = depth;
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
			this.xCenter = (xMin + xMax) * 0.5;
			this.yCenter = (yMin + yMax) * 0.5;
			objects = new UnorderedIdentityArrayList<Transform>(MAX_OBJECTS);
		}

		public void add(double x, double y, Transform object) {
			if (objects.size() > MAX_OBJECTS && depth < MAX_DEPTH) {
				if (!hasChilds())
					createChilds();
				getChild(x, y).add(x, y, object);
				return;
			}
			objects.add(object);
		}

		public void remove(double x, double y, Transform object) {
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

		public void move(double xOld, double yOld, double xNew, double yNew, Transform object) {
			int index = objects.indexOf(object);
			if (index == -1 && hasChilds()) {
				Node oldNode = getChild(xOld, yOld);
				Node newNode = getChild(xNew, yNew);
				if (oldNode == newNode)
					oldNode.move(xOld, yOld, xNew, yNew, object);
				else {
					oldNode.remove(xOld, yOld, object);
					newNode.add(xNew, yNew, object);
				}
			}
		}

		public void forEach(double xMin, double yMin, double xMax, double yMax, Consumer<Transform> func) {
			for (Transform object : objects)
				if (object.getX() >= xMin && object.getY() >= yMin && object.getX() < xMax && object.getY() < yMax)
					func.accept(object);

			if (hasChilds()) {
				if (yMin <= yCenter) {
					if (xMin <= xCenter)
						childSW.forEach(xMin, yMin, xMax, yMax, func);
					if (xMax > xCenter)
						childSO.forEach(xMin, yMin, xMax, yMax, func);
				}
				if (yMax > yCenter) {
					if (xMin <= xCenter)
						childNW.forEach(xMin, yMin, xMax, yMax, func);
					if (xMax > xCenter)
						childNO.forEach(xMin, yMin, xMax, yMax, func);
				}
			}
		}

		public Transform get(double x, double y, Transform opt_default) {
			for (Transform object : objects)
				if (object.getX() == x && object.getY() == y)
					return object;

			if (hasChilds())
				return getChild(x, y).get(x, y, opt_default);
			return opt_default;
		}

		public boolean contains(double x, double y, Transform object) {
			int index = objects.indexOf(object);
			if (index != -1)
				return true;

			if (hasChilds())
				return getChild(x, y).contains(x, y, object);
			return false;
		}

		public boolean isEmpty() {
			return objects.isEmpty();
		}

		public void clear() {
			objects.clear();
			deleteChilds();
		}

		protected void createChilds() {
			childSW = new Node(depth + 1, xMin, yMin, xCenter, yCenter);
			childSO = new Node(depth + 1, xCenter, yMin, xMax, yCenter);
			childNW = new Node(depth + 1, xMin, yCenter, xCenter, yMax);
			childNO = new Node(depth + 1, xCenter, yCenter, xMax, yMax);
		}

		protected void deleteChilds() {
			childSW = null;
			childSO = null;
			childNW = null;
			childNO = null;
		}

		protected Node getChild(double x, double y) {
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
