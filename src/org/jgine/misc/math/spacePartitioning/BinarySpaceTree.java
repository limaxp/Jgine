package org.jgine.misc.math.spacePartitioning;

import java.util.List;

public class BinarySpaceTree<T> {

	public static class Node<T> {

		private static final int depth_max = 32;
		private static final int max_objects = 32;
		
//		private final int depth;
//		private final int min, max, center;
		private List<T> objects;
		private Node<T> childA;
		private Node<T> childB;
	}

	public static class BinarySpaceTreePoint<T> implements Comparable<BinarySpaceTreePoint<T>> {

		private double x;
		private double y;
		private T opt_value;

		public BinarySpaceTreePoint(double x, double y, T opt_value) {
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
		public int compareTo(BinarySpaceTreePoint<T> point) {
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
}
