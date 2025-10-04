package org.jgine.utils.collection.tree;

import java.util.ArrayList;
import java.util.List;

public class QuadTree<T> {

	private final Node<T> root;
	private int count = 0;

	/**
	 * Constructs a new quad tree.
	 *
	 * @param {double} minX Minimum x-value that can be held in tree.
	 * @param {double} minY Minimum y-value that can be held in tree.
	 * @param {double} maxX Maximum x-value that can be held in tree.
	 * @param {double} maxY Maximum y-value that can be held in tree.
	 */
	public QuadTree(double minX, double minY, double maxX, double maxY) {
		this.root = new Node<T>(minX, minY, maxX - minX, maxY - minY, null);
	}

	/**
	 * Returns a reference to the tree's root node. Callers shouldn't modify nodes,
	 * directly. This is a convenience for visualization and debugging purposes.
	 *
	 * @return {Node} The root node.
	 */
	public Node<T> getRootNode() {
		return this.root;
	}

	/**
	 * Sets the value of an (x, y) point within the quad-tree.
	 *
	 * @param {double} x The x-coordinate.
	 * @param {double} y The y-coordinate.
	 * @param {T}      value The value associated with the point.
	 */
	public void set(double x, double y, T value) {

		Node<T> root = this.root;
		if (x < root.getX() || y < root.getY() || x > root.getX() + root.getW() || y > root.getY() + root.getH()) {
			throw new QuadTreeException("Out of bounds : (" + x + ", " + y + ")");
		}
		if (this.insert(root, new QuadTreePoint<T>(x, y, value))) {
			this.count++;
		}
	}

	/**
	 * Gets the value of the point at (x, y) or null if the point is empty.
	 *
	 * @param {double} x The x-coordinate.
	 * @param {double} y The y-coordinate.
	 * @param {T}      opt_default The default value to return if the node doesn't
	 *                 exist.
	 * @return {*} The value of the node, the default value if the node doesn't
	 *         exist, or undefined if the node doesn't exist and no default has been
	 *         provided.
	 */
	public T get(double x, double y, T opt_default) {
		Node<T> node = this.find(this.root, x, y);
		return node != null ? node.getPoint().getValue() : opt_default;
	}

	/**
	 * Removes a point from (x, y) if it exists.
	 *
	 * @param {double} x The x-coordinate.
	 * @param {double} y The y-coordinate.
	 * @return {T} The value of the node that was removed, or null if the node
	 *         doesn't exist.
	 */
	public T remove(double x, double y) {
		Node<T> node = this.find(this.root, x, y);
		if (node != null) {
			T value = node.getPoint().getValue();
			node.setPoint(null);
			node.setNodeType(NodeType.EMPTY);
			this.balance(node);
			this.count--;
			return value;
		} else {
			return null;
		}
	}

	/**
	 * Returns true if the point at (x, y) exists in the tree.
	 *
	 * @param {double} x The x-coordinate.
	 * @param {double} y The y-coordinate.
	 * @return {boolean} Whether the tree contains a point at (x, y).
	 */
	public boolean contains(double x, double y) {
		return this.get(x, y, null) != null;
	}

	/**
	 * @return {boolean} Whether the tree is empty.
	 */
	public boolean isEmpty() {
		return this.root.getNodeType() == NodeType.EMPTY;
	}

	/**
	 * @return {number} The number of items in the tree.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Removes all items from the tree.
	 */
	public void clear() {
		this.root.setNw(null);
		this.root.setNe(null);
		this.root.setSw(null);
		this.root.setSe(null);
		this.root.setNodeType(NodeType.EMPTY);
		this.root.setPoint(null);
		this.count = 0;
	}

	/**
	 * Returns an array containing the coordinates of each point stored in the tree.
	 * 
	 * @return {Array.<Point>} Array of coordinates.
	 */
	@SuppressWarnings("unchecked")
	public QuadTreePoint<T>[] getKeys() {
		final List<QuadTreePoint<T>> arr = new ArrayList<QuadTreePoint<T>>();
		this.traverse(this.root, new QuadTreeFunc<T>() {

			@Override
			public void call(QuadTree<T> quadTree, Node<T> node) {
				arr.add(node.getPoint());
			}
		});
		return arr.toArray((QuadTreePoint<T>[]) new QuadTreePoint[arr.size()]);
	}

	/**
	 * Returns a list containing all values stored within the tree.
	 * 
	 * @return {List<T>} The values stored within the tree.
	 */
	public List<T> getValues() {
		final List<T> arr = new ArrayList<T>();
		this.traverse(this.root, new QuadTreeFunc<T>() {

			@Override
			public void call(QuadTree<T> quadTree, Node<T> node) {
				arr.add(node.getPoint().getValue());
			}
		});

		return arr;
	}

	@SuppressWarnings("unchecked")
	public QuadTreePoint<T>[] searchIntersect(final double xmin, final double ymin, final double xmax,
			final double ymax) {
		final List<QuadTreePoint<T>> arr = new ArrayList<QuadTreePoint<T>>();
		this.navigate(this.root, new QuadTreeFunc<T>() {

			@Override
			public void call(QuadTree<T> quadTree, Node<T> node) {
				QuadTreePoint<T> pt = node.getPoint();
				if (pt.getX() < xmin || pt.getX() > xmax || pt.getY() < ymin || pt.getY() > ymax) {
					// Definitely not within the polygon!
				} else {
					arr.add(node.getPoint());
				}

			}
		}, xmin, ymin, xmax, ymax);
		return arr.toArray((QuadTreePoint<T>[]) new QuadTreePoint[arr.size()]);
	}

	@SuppressWarnings("unchecked")
	public QuadTreePoint<T>[] searchWithin(final double xmin, final double ymin, final double xmax, final double ymax) {
		final List<QuadTreePoint<T>> arr = new ArrayList<QuadTreePoint<T>>();
		this.navigate(this.root, new QuadTreeFunc<T>() {

			@Override
			public void call(QuadTree<T> quadTree, Node<T> node) {
				QuadTreePoint<T> pt = node.getPoint();
				if (pt.getX() > xmin && pt.getX() < xmax && pt.getY() > ymin && pt.getY() < ymax) {
					arr.add(node.getPoint());
				}
			}
		}, xmin, ymin, xmax, ymax);
		return arr.toArray((QuadTreePoint<T>[]) new QuadTreePoint[arr.size()]);
	}

	public void navigate(Node<T> node, QuadTreeFunc<T> func, double xmin, double ymin, double xmax, double ymax) {
		switch (node.getNodeType()) {
		case LEAF:
			func.call(this, node);
			break;

		case POINTER:
			if (intersects(xmin, ymax, xmax, ymin, node.getNe()))
				this.navigate(node.getNe(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getSe()))
				this.navigate(node.getSe(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getSw()))
				this.navigate(node.getSw(), func, xmin, ymin, xmax, ymax);
			if (intersects(xmin, ymax, xmax, ymin, node.getNw()))
				this.navigate(node.getNw(), func, xmin, ymin, xmax, ymax);
			break;

		default:
			break;
		}
	}

	private boolean intersects(double left, double bottom, double right, double top, Node<T> node) {
		return !(node.getX() > right || (node.getX() + node.getW()) < left || node.getY() > bottom
				|| (node.getY() + node.getH()) < top);
	}

	/**
	 * Clones the quad-tree and returns the new instance.
	 * 
	 * @return {QuadTree} A clone of the tree.
	 */
	@Override
	public QuadTree<T> clone() {
		double x1 = this.root.getX();
		double y1 = this.root.getY();
		double x2 = x1 + this.root.getW();
		double y2 = y1 + this.root.getH();
		final QuadTree<T> clone = new QuadTree<T>(x1, y1, x2, y2);
		// This is inefficient as the clone needs to recalculate the structure of the
		// tree, even though we know it already. But this is easier and can be
		// optimized when/if needed.
		this.traverse(this.root, new QuadTreeFunc<T>() {

			@Override
			public void call(QuadTree<T> quadTree, Node<T> node) {
				clone.set(node.getPoint().getX(), node.getPoint().getY(), node.getPoint().getValue());
			}
		});

		return clone;
	}

	/**
	 * Traverses the tree depth-first, with quadrants being traversed in clockwise
	 * order (NE, SE, SW, NW). The provided function will be called for each leaf
	 * node that is encountered.
	 * 
	 * @param {QuadTree.Node}           node The current node.
	 * @param {function(QuadTree.Node)} fn The function to call for each leaf node.
	 *                                  This function takes the node as an argument,
	 *                                  and its return value is irrelevant.
	 * @private
	 */
	public void traverse(Node<T> node, QuadTreeFunc<T> func) {
		switch (node.getNodeType()) {
		case LEAF:
			func.call(this, node);
			break;

		case POINTER:
			this.traverse(node.getNe(), func);
			this.traverse(node.getSe(), func);
			this.traverse(node.getSw(), func);
			this.traverse(node.getNw(), func);
			break;

		default:
			break;
		}
	}

	/**
	 * Finds a leaf node with the same (x, y) coordinates as the target point, or
	 * null if no point exists.
	 * 
	 * @param {QuadTree.Node} node The node to search in.
	 * @param {number}        x The x-coordinate of the point to search for.
	 * @param {number}        y The y-coordinate of the point to search for.
	 * @return {QuadTree.Node} The leaf node that matches the target, or null if it
	 *         doesn't exist.
	 * @private
	 */
	public Node<T> find(Node<T> node, double x, double y) {
		Node<T> resposne = null;
		switch (node.getNodeType()) {
		case EMPTY:
			break;

		case LEAF:
			resposne = node.getPoint().getX() == x && node.getPoint().getY() == y ? node : null;
			break;

		case POINTER:
			resposne = this.find(this.getQuadrantForPoint(node, x, y), x, y);
			break;

		default:
			throw new QuadTreeException("Invalid nodeType");
		}
		return resposne;
	}

	/**
	 * Inserts a point into the tree, updating the tree's structure if necessary.
	 * 
	 * @param {.QuadTree.Node} parent The parent to insert the point into.
	 * @param {QuadTree.Point} point The point to insert.
	 * @return {boolean} True if a new node was added to the tree; False if a node
	 *         already existed with the correpsonding coordinates and had its value
	 *         reset.
	 * @private
	 */
	private boolean insert(Node<T> parent, QuadTreePoint<T> point) {
		Boolean result = false;
		switch (parent.getNodeType()) {
		case EMPTY:
			this.setPointForNode(parent, point);
			result = true;
			break;
		case LEAF:
			if (parent.getPoint().getX() == point.getX() && parent.getPoint().getY() == point.getY()) {
				this.setPointForNode(parent, point);
				result = false;
			} else {
				this.split(parent);
				result = this.insert(parent, point);
			}
			break;
		case POINTER:
			result = this.insert(this.getQuadrantForPoint(parent, point.getX(), point.getY()), point);
			break;

		default:
			throw new QuadTreeException("Invalid nodeType in parent");
		}
		return result;
	}

	/**
	 * Converts a leaf node to a pointer node and reinserts the node's point into
	 * the correct child.
	 * 
	 * @param {QuadTree.Node} node The node to split.
	 * @private
	 */
	private void split(Node<T> node) {
		QuadTreePoint<T> oldPoint = node.getPoint();
		node.setPoint(null);

		node.setNodeType(NodeType.POINTER);

		double x = node.getX();
		double y = node.getY();
		double hw = node.getW() / 2;
		double hh = node.getH() / 2;

		node.setNw(new Node<T>(x, y, hw, hh, node));
		node.setNe(new Node<T>(x + hw, y, hw, hh, node));
		node.setSw(new Node<T>(x, y + hh, hw, hh, node));
		node.setSe(new Node<T>(x + hw, y + hh, hw, hh, node));

		this.insert(node, oldPoint);
	}

	/**
	 * Attempts to balance a node. A node will need balancing if all its children
	 * are empty or it contains just one leaf.
	 * 
	 * @param {QuadTree.Node} node The node to balance.
	 * @private
	 */
	private void balance(Node<T> node) {
		switch (node.getNodeType()) {
		case EMPTY:
		case LEAF:
			if (node.getParent() != null) {
				this.balance(node.getParent());
			}
			break;

		case POINTER: {
			Node<T> nw = node.getNw();
			Node<T> ne = node.getNe();
			Node<T> sw = node.getSw();
			Node<T> se = node.getSe();
			Node<T> firstLeaf = null;

			// Look for the first non-empty child, if there is more than one then we
			// break as this node can't be balanced.
			if (nw.getNodeType() != NodeType.EMPTY) {
				firstLeaf = nw;
			}
			if (ne.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = ne;
			}
			if (sw.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = sw;
			}
			if (se.getNodeType() != NodeType.EMPTY) {
				if (firstLeaf != null) {
					break;
				}
				firstLeaf = se;
			}

			if (firstLeaf == null) {
				// All child nodes are empty: so make this node empty.
				node.setNodeType(NodeType.EMPTY);
				node.setNw(null);
				node.setNe(null);
				node.setSw(null);
				node.setSe(null);

			} else if (firstLeaf.getNodeType() == NodeType.POINTER) {
				// Only child was a pointer, therefore we can't rebalance.
				break;

			} else {
				// Only child was a leaf: so update node's point and make it a leaf.
				node.setNodeType(NodeType.LEAF);
				node.setNw(null);
				node.setNe(null);
				node.setSw(null);
				node.setSe(null);
				node.setPoint(firstLeaf.getPoint());
			}

			// Try and balance the parent as well.
			if (node.getParent() != null) {
				this.balance(node.getParent());
			}
		}
			break;
		}
	}

	/**
	 * Returns the child quadrant within a node that contains the given (x, y)
	 * coordinate.
	 * 
	 * @param {QuadTree.Node} parent The node.
	 * @param {number}        x The x-coordinate to look for.
	 * @param {number}        y The y-coordinate to look for.
	 * @return {QuadTree.Node} The child quadrant that contains the point.
	 * @private
	 */
	private Node<T> getQuadrantForPoint(Node<T> parent, double x, double y) {
		double mx = parent.getX() + parent.getW() / 2;
		double my = parent.getY() + parent.getH() / 2;
		if (x < mx) {
			return y < my ? parent.getNw() : parent.getSw();
		} else {
			return y < my ? parent.getNe() : parent.getSe();
		}
	}

	/**
	 * Sets the point for a node, as long as the node is a leaf or empty.
	 * 
	 * @param {QuadTree.Node}  node The node to set the point for.
	 * @param {QuadTree.Point} point The point to set.
	 * @private
	 */
	private void setPointForNode(Node<T> node, QuadTreePoint<T> point) {
		if (node.getNodeType() == NodeType.POINTER) {
			throw new QuadTreeException("Can not set point for node of type POINTER");
		}
		node.setNodeType(NodeType.LEAF);
		node.setPoint(point);
	}

	public static enum NodeType {
		EMPTY, LEAF, POINTER
	}

	public static class Node<T> {

		private double x;
		private double y;
		private double w;
		private double h;
		private Node<T> opt_parent;
		private QuadTreePoint<T> point;
		private NodeType nodetype = NodeType.EMPTY;
		private Node<T> nw;
		private Node<T> ne;
		private Node<T> sw;
		private Node<T> se;

		public Node(double x, double y, double w, double h, Node<T> opt_parent) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.opt_parent = opt_parent;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getW() {
			return w;
		}

		public void setW(double w) {
			this.w = w;
		}

		public double getH() {
			return h;
		}

		public void setH(double h) {
			this.h = h;
		}

		public Node<T> getParent() {
			return opt_parent;
		}

		public void setParent(Node<T> opt_parent) {
			this.opt_parent = opt_parent;
		}

		public void setPoint(QuadTreePoint<T> point) {
			this.point = point;
		}

		public QuadTreePoint<T> getPoint() {
			return this.point;
		}

		public void setNodeType(NodeType nodetype) {
			this.nodetype = nodetype;
		}

		public NodeType getNodeType() {
			return this.nodetype;
		}

		public void setNw(Node<T> nw) {
			this.nw = nw;
		}

		public void setNe(Node<T> ne) {
			this.ne = ne;
		}

		public void setSw(Node<T> sw) {
			this.sw = sw;
		}

		public void setSe(Node<T> se) {
			this.se = se;
		}

		public Node<T> getNe() {
			return ne;
		}

		public Node<T> getNw() {
			return nw;
		}

		public Node<T> getSw() {
			return sw;
		}

		public Node<T> getSe() {
			return se;
		}
	}

	public static class QuadTreePoint<T> implements Comparable<QuadTreePoint<T>> {

		private double x;
		private double y;
		private T opt_value;

		public QuadTreePoint(double x, double y, T opt_value) {
			this.x = x;
			this.y = y;
			this.opt_value = opt_value;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public T getValue() {
			return opt_value;
		}

		public void setValue(T opt_value) {
			this.opt_value = opt_value;
		}

		@Override
		public String toString() {
			return "(" + this.x + ", " + this.y + ")";
		}

		@Override
		public int compareTo(QuadTreePoint<T> point) {
			if (this.x < point.x) {
				return -1;
			} else if (this.x > point.x) {
				return 1;
			} else {
				if (this.y < point.y) {
					return -1;
				} else if (this.y > point.y) {
					return 1;
				}
				return 0;
			}
		}
	}

	public static class QuadTreeException extends RuntimeException {

		private static final long serialVersionUID = -2820593983833193502L;

		public QuadTreeException(String s) {
			super(s);
		}
	}

	public static interface QuadTreeFunc<T> {

		public void call(QuadTree<T> quadTree, Node<T> node);
	}
}
