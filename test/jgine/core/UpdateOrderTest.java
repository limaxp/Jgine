package jgine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
		assertTrue(updateOrder.getStart().contains(sysA));
	}

	@Test
	void testAddBeforeAndAfterSingle() {
		UpdateOrder updateOrder = new UpdateOrder(2);
		updateOrder.add(sysA, sysB); // sysA before sysB
		assertTrue(updateOrder.getParents(sysA).contains(sysB));
		assertTrue(updateOrder.getChilds(sysB).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeAndAfterMultiple() {
		UpdateOrder updateOrder = new UpdateOrder(3);
		updateOrder.add(sysA, sysB, sysC);
		assertTrue(updateOrder.getParents(sysA).contains(sysB));
		assertTrue(updateOrder.getParents(sysA).contains(sysC));
		assertTrue(updateOrder.getChilds(sysB).contains(sysA));
		assertTrue(updateOrder.getChilds(sysC).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeCollection() {
		UpdateOrder updateOrder = new UpdateOrder(3);
		List<Integer> list = Arrays.asList(sysB, sysC);
		updateOrder.add(sysA, sysB, sysC);
		assertTrue(updateOrder.getParents(sysA).containsAll(list));
		assertTrue(updateOrder.getChilds(sysB).contains(sysA));
		assertTrue(updateOrder.getChilds(sysC).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testSaveAndLoad() throws IOException {
		UpdateOrder updateOrder = new UpdateOrder(3);
		updateOrder.add(sysA);
		updateOrder.add(sysB, sysA);
		updateOrder.add(sysC, sysA, sysB);

		// Save to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		updateOrder.save(out);

		// Load into a new object
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream in = new DataInputStream(bais);
		UpdateOrder loaded = new UpdateOrder(3);
		loaded.load(in);

		assertEquals(updateOrder.size(), loaded.size());
		assertEquals(updateOrder.getStart().size(), loaded.getStart().size());
		assertEquals(updateOrder.getParents(sysA).size(), loaded.getParents(sysA).size());
		assertEquals(updateOrder.getChilds(sysB).size(), loaded.getChilds(sysB).size());
	}
}
