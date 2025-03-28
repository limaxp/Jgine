package org.jgine.utils.collection.tree;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BinaryTreeTest {

	private BinaryTree<Integer> createBinaryTree() {
		BinaryTree<Integer> bt = new BinaryTree<Integer>();
		bt.add(6);
		bt.add(4);
		bt.add(8);
		bt.add(3);
		bt.add(5);
		bt.add(7);
		bt.add(9);
		return bt;
	}

	@Test
	void whenAddingElements_ThenContainsThoseElements() {
		BinaryTree<Integer> bt = createBinaryTree();
		assertTrue(bt.contains(6));
		assertTrue(bt.contains(4));
		assertTrue(bt.contains(8));
		assertTrue(bt.contains(3));
		assertTrue(bt.contains(4));
		assertTrue(bt.contains(5));
		assertTrue(bt.contains(9));
		assertFalse(bt.contains(1));
		assertFalse(bt.contains(2));
	}

	@Test
	public void whenDeletingElements_ThenDoesNotContainThoseElements() {
		BinaryTree<Integer> bt = createBinaryTree();
		assertTrue(bt.contains(9));
		bt.remove(9);
		assertFalse(bt.contains(9));
	}
}
