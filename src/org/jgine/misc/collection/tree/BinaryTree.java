package org.jgine.misc.collection.tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class BinaryTree<E extends Comparable<E>> {

	private Node<E> root;

	public BinaryTree() {}

	public void add(E value) {
		root = addRecursive(root, value);
	}

	public void remove(E value) {
		root = deleteRecursive(root, value);
	}

	public boolean contains(E value) {
		return containsRecursive(root, value);
	}

	public boolean isEmpty() {
		return root == null;
	}

	public void clear() {
		root = null;
	}

	private Node<E> addRecursive(Node<E> node, E value) {
		if (node == null)
			return new Node<E>(value);
		int compare = value.compareTo(node.value);
		if (compare < 0)
			node.left = addRecursive(node.left, value);
		else if (compare > 0)
			node.right = addRecursive(node.right, value);
		else {
			// value already exists
			return node;
		}
		return node;
	}

	private boolean containsRecursive(Node<E> node, E value) {
		if (node == null)
			return false;
		if (value == node.value)
			return true;
		int compare = value.compareTo(node.value);
		return compare < 0
				? containsRecursive(node.left, value)
				: containsRecursive(node.right, value);
	}

	private Node<E> deleteRecursive(Node<E> node, E value) {
		if (node == null)
			return null;
		if (value == node.value) {
			if (node.left == null && node.right == null)
				return null;
			if (node.right == null)
				return node.left;
			if (node.left == null)
				return node.right;

			E smallestValue = findSmallestValue(node.right);
			node.value = smallestValue;
			node.right = deleteRecursive(node.right, smallestValue);
			return node;
		}
		int compare = value.compareTo(node.value);
		if (compare < 0) {
			node.left = deleteRecursive(node.left, value);
			return node;
		}
		node.right = deleteRecursive(node.right, value);
		return node;
	}

	private E findSmallestValue(Node<E> root) {
		return root.left == null ? root.value : findSmallestValue(root.left);
	}

	public void traverseInOrder(Consumer<E> consumer) {
		traverseInOrder(root, consumer);
	}

	private void traverseInOrder(Node<E> node, Consumer<E> consumer) {
		if (node != null) {
			traverseInOrder(node.left, consumer);
			consumer.accept(node.value);
			traverseInOrder(node.right, consumer);
		}
	}

	public void traversePreOrder(Consumer<E> consumer) {
		traversePreOrder(root, consumer);
	}

	private void traversePreOrder(Node<E> node, Consumer<E> consumer) {
		if (node != null) {
			consumer.accept(node.value);
			traversePreOrder(node.left, consumer);
			traversePreOrder(node.right, consumer);
		}
	}

	public void traversePostOrder(Consumer<E> consumer) {
		traversePostOrder(root, consumer);
	}

	private void traversePostOrder(Node<E> node, Consumer<E> consumer) {
		if (node != null) {
			traversePostOrder(node.left, consumer);
			traversePostOrder(node.right, consumer);
			consumer.accept(node.value);
		}
	}

	public void traverseLevelOrder(Consumer<E> consumer) {
		if (root == null)
			return;
		Queue<Node<E>> nodes = new LinkedList<>();
		nodes.add(root);
		while (!nodes.isEmpty()) {
			Node<E> node = nodes.remove();
			consumer.accept(node.value);
			if (node.left != null)
				nodes.add(node.left);
			if (node.right != null)
				nodes.add(node.right);
		}
	}

	public void traverseFrontToBack(E value, Consumer<E> consumer) {
		traverseFrontToBack(root, value, consumer);
	}

	private void traverseFrontToBack(Node<E> node, E value, Consumer<E> consumer) {
		if (node == null)
			return;
		int compare = value.compareTo(node.value);
		if (compare < 0) {
			// print in order
			traverseFrontToBack(node.left, value, consumer);
			consumer.accept(node.value);
			traverseFrontToBack(node.right, value, consumer);
		}
		else if (compare > 0) {
			// print in reverse order
			traverseFrontToBack(node.right, value, consumer);
			consumer.accept(node.value);
			traverseFrontToBack(node.left, value, consumer);
		}
		else {
			// order doesn't matter
			traverseFrontToBack(node.left, value, consumer);
			traverseFrontToBack(node.right, value, consumer);
		}
	}

	private static class Node<E> {

		private Node<E> left;
		private Node<E> right;
		private E value;

		public Node(E value) {
			this.value = value;
		}
	}
}
