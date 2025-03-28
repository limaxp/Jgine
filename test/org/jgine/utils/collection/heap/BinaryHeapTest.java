package org.jgine.utils.collection.heap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

class BinaryHeapTest {

	class StringHeapObject implements IHeapItem<StringHeapObject> {

		private int heapIndex;
		public String string;

		public StringHeapObject(String string) {
			this.string = string;
		}

		@Override
		public int compareTo(StringHeapObject arg0) {
			return string.length() - arg0.string.length();
		}

		@Override
		public int getHeapIndex() {
			return heapIndex;
		}

		@Override
		public void setHeapIndex(int index) {
			heapIndex = index;
		}
	}

	BinaryHeap<StringHeapObject> heap = new BinaryHeap.BinaryMinHeap<StringHeapObject>(new StringHeapObject[10]);

	StringHeapObject a = new StringHeapObject("Key");
	StringHeapObject b = new StringHeapObject("Key2");
	StringHeapObject c = new StringHeapObject("Key_3");
	StringHeapObject d = new StringHeapObject("Key__4");
	StringHeapObject e = new StringHeapObject("Key___5");
	StringHeapObject f = new StringHeapObject("Key____6");
	StringHeapObject g = new StringHeapObject("Key_____7");
	StringHeapObject h = new StringHeapObject("Key______8");
	StringHeapObject i = new StringHeapObject("Key_______9");
	StringHeapObject j = new StringHeapObject("Key_______10");
	StringHeapObject k = new StringHeapObject("Key________11");

	@Test
	void whenPushingElements_ThenAddingThoseElements() {
		heap.push(a);
		heap.push(b);
		heap.push(c);
		heap.push(d);
		heap.push(e);
		assertEquals(heap.array[0], a);
		assertEquals(heap.array[1], b);
		assertEquals(heap.array[2], c);
		assertEquals(heap.array[3], d);
		assertEquals(heap.array[4], e);
		assertTrue(heap.size() == 5);
	}

	@Test
	void whenPopingElements_ThenDoesNotContainThoseElements() {
		heap.push(e);
		heap.push(a);
		heap.push(d);
		heap.push(c);
		heap.push(b);
		assertEquals(heap.pop(), a);
		assertEquals(heap.pop(), b);
		assertEquals(heap.pop(), c);
		assertEquals(heap.pop(), d);
		assertEquals(heap.pop(), e);
	}

	@Test
	void whenPopingAllElements_ThenIsEmpty() {
		heap.push(e);
		heap.push(a);
		heap.push(d);
		heap.push(c);
		heap.push(b);
		heap.pop();
		heap.pop();
		heap.pop();
		heap.pop();
		heap.pop();
		assertTrue(heap.isEmpty());
	}

	@Test
	void whenPushingElements_ThenContainsThoseElements() {
		heap.push(a);
		assertTrue(heap.contains(a));
	}

	@Test
	void whenPushingElements_ThenElementsCanBeGet() {
		heap.push(a);
		heap.push(b);
		heap.push(c);
		assertEquals(a, heap.get(0));
	}

	@Test
	void afterIteratingElements_IteratorIsEmpty() {
		heap.push(a);
		heap.push(b);
		heap.push(c);
		Iterator<StringHeapObject> iterator = heap.iterator();
		assertEquals(iterator.next(), a);
		assertEquals(iterator.next(), b);
		assertEquals(iterator.next(), c);
		assertFalse(iterator.hasNext());
	}

	@Test
	void whenClearingAllElements_ThenIsEmpty() {
		heap.push(a);
		heap.push(b);
		heap.push(c);
		heap.clear();
		assertTrue(heap.isEmpty());
	}
}
