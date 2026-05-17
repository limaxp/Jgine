package jgine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class UpdateOrderTest {

	private final int sysA = 0;
	private final int sysB = 1;
	private final int sysC = 2;

	@Test
	void testAddStartSystem() {
		UpdateOrder updateOrder = new UpdateOrder(1);
		updateOrder.add(sysA);
		assertEquals(1, updateOrder.size());
		assertTrue(updateOrder.isStart(sysA));
	}

	@Test
	void testAddBeforeAndAfterSingle() {
		UpdateOrder updateOrder = new UpdateOrder(2);
		updateOrder.add(sysA, sysB);
		assertTrue(updateOrder.hasParent(sysA, sysB));
		assertTrue(updateOrder.hasChild(sysB, sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeAndAfterMultiple() {
		UpdateOrder updateOrder = new UpdateOrder(3);
		updateOrder.add(sysA, sysB, sysC);
		assertTrue(updateOrder.hasParent(sysA, sysB));
		assertTrue(updateOrder.hasParent(sysA, sysC));
		assertTrue(updateOrder.hasChild(sysB, sysA));
		assertTrue(updateOrder.hasChild(sysC, sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeCollection() {
		UpdateOrder updateOrder = new UpdateOrder(3);
		updateOrder.add(sysA, sysB, sysC);
		assertTrue(updateOrder.hasParent(sysA, sysB));
		assertTrue(updateOrder.hasParent(sysA, sysC));
		assertTrue(updateOrder.hasChild(sysB, sysA));
		assertTrue(updateOrder.hasChild(sysC, sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testSaveAndLoad() throws IOException {
		UpdateOrder updateOrder = new UpdateOrder(3);
		updateOrder.add(sysA);
		updateOrder.add(sysB, sysA);
		updateOrder.add(sysC, sysA, sysB);

		UpdateOrder loaded = new UpdateOrder(3);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(baos)) {
			updateOrder.save(out);

			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					DataInputStream in = new DataInputStream(bais)) {
				loaded.load(in);
			}
		}

		assertEquals(updateOrder.size(), loaded.size());
		assertTrue(loaded.isStart(sysA));
		assertTrue(loaded.hasChild(sysA, sysB));
		assertTrue(loaded.hasParent(sysB, sysA));

		assertTrue(loaded.hasChild(sysA, sysC));
		assertTrue(loaded.hasChild(sysB, sysC));
		assertTrue(loaded.hasParent(sysC, sysA));
		assertTrue(loaded.hasParent(sysC, sysB));
	}
}
