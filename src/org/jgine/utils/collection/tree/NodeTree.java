package org.jgine.utils.collection.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.collection.list.UnorderedIdentityArrayList;

public class NodeTree<E> implements Tree<E> {

	private Node<E> root;

	public NodeTree(E root) {
		this.root = new Node<E>(root);
	}

	public void setRoot(E root) {
		if (this.root != null)
			clear();
		this.root = new Node<E>(root);
	}

	public Node<E> getRootNode() {
		return root;
	}

	@Override
	public boolean add(E arg0) {
		root.add(arg0);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		root.addAll(arg0);
		return true;
	}

	@Override
	public void clear() {
		root.clear();
		root = null;
	}

	@Override
	public boolean contains(Object arg0) {
		return root.contains(arg0) ? true : false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return root.containsAll(arg0) ? true : false;
	}

	@Override
	public boolean isEmpty() {
		return root == null ? true : false;
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object arg0) {
		return root.remove(arg0) != null ? true : false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return root.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}

	public static class Node<E> {

		private Node<E> parent;
		private List<Node<E>> childs;
		private E value;

		public Node(E value) {
			childs = new UnorderedIdentityArrayList<Node<E>>();
			this.value = value;
		}

		public Node<E> add(E value) {
			Node<E> node = new Node<E>(value);
			childs.add(node);
			node.parent = this;
			return node;
		}

		public void addAll(Collection<? extends E> values) {
			for (E value : values)
				add(value);
		}

		@Nullable
		public Node<E> remove(Object value) {
			int index = childs.indexOf(value);
			if (index >= 0) {
				Node<E> node = childs.remove(index);
				node.parent = this;
				return node;
			}
			return null;
		}

		public boolean removeAll(Collection<?> values) {
			boolean changed = false;
			for (Object value : values)
				if (remove(value) != null)
					changed = true;
			return changed;
		}

		public void clear() {
			for (Node<E> child : childs)
				child.clear();
			childs.clear();
			parent = null;
			value = null;
		}

		public boolean contains(Object value) {
			if (this.value.equals(value))
				return true;
			for (Node<E> child : childs) {
				if (child.contains(value))
					return true;
			}
			return false;
		}

		public boolean containsAll(Collection<?> values) {
			for (Object value : values)
				if (!contains(value))
					return false;
			return true;
		}

		@Nullable
		public Node<E> getParent() {
			return parent;
		}

		public List<Node<E>> getChilds() {
			return Collections.unmodifiableList(childs);
		}

		public E getValue() {
			return value;
		}
	}
}
