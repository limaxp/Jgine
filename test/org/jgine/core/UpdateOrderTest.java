package org.jgine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jgine.system.EngineSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateOrderTest {

	private UpdateOrder updateOrder;
	private EngineSystem<?, ?> sysA;
	private EngineSystem<?, ?> sysB;
	private EngineSystem<?, ?> sysC;

	@BeforeEach
	void setup() {
		sysA = Engine.PHYSIC_SYSTEM;
		sysB = Engine.COLLISION_SYSTEM;
		sysC = Engine.AI_SYSTEM;
		updateOrder = new UpdateOrder();
	}

	@Test
	void testAddStartSystem() {
		updateOrder.add(sysA);
		assertEquals(1, updateOrder.size());
		assertTrue(updateOrder.getStart().contains(sysA));
	}

	@Test
	void testAddBeforeAndAfterSingle() {
		updateOrder.add(sysA, sysB); // sysA before sysB
		assertTrue(updateOrder.getBefore(sysA).contains(sysB));
		assertTrue(updateOrder.getAfter(sysB).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeAndAfterMultiple() {
		updateOrder.add(sysA, sysB, sysC);
		assertTrue(updateOrder.getBefore(sysA).contains(sysB));
		assertTrue(updateOrder.getBefore(sysA).contains(sysC));
		assertTrue(updateOrder.getAfter(sysB).contains(sysA));
		assertTrue(updateOrder.getAfter(sysC).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testAddBeforeCollection() {
		List<EngineSystem<?, ?>> list = Arrays.asList(sysB, sysC);
		updateOrder.add(sysA, list);
		assertTrue(updateOrder.getBefore(sysA).containsAll(list));
		assertTrue(updateOrder.getAfter(sysB).contains(sysA));
		assertTrue(updateOrder.getAfter(sysC).contains(sysA));
		assertEquals(1, updateOrder.size());
	}

	@Test
	void testSaveAndLoad() throws IOException {
		updateOrder.add(sysA);
		updateOrder.add(sysB, sysA);
		updateOrder.add(sysC, Arrays.asList(sysA, sysB));

		// Save to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		updateOrder.save(out);

		// Load into a new object
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream in = new DataInputStream(bais);
		UpdateOrder loaded = new UpdateOrder();
		loaded.load(in);

		assertEquals(updateOrder.size(), loaded.size());
		assertEquals(updateOrder.getStart().size(), loaded.getStart().size());
		assertEquals(updateOrder.getBefore(sysA).size(), loaded.getBefore(sysA).size());
		assertEquals(updateOrder.getAfter(sysB).size(), loaded.getAfter(sysB).size());
	}
}
