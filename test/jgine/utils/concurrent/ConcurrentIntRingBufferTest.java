package jgine.utils.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import jgine.utils.math.FastMath;

public class ConcurrentIntRingBufferTest {

	@Test
	void whenPushingElements_ThenAddingThoseElements() {
		final int size = 16;
		final ConcurrentIntRingBuffer buffer = new ConcurrentIntRingBuffer(size);
		assertEquals(buffer.poll(), ConcurrentIntRingBuffer.EMPTY);
		for (int i = 0; i < size; i++)
			assertEquals(buffer.add(i + 56436), true);
		for (int i = 0; i < size; i++)
			assertEquals(buffer.poll(), i + 56436);
		assertEquals(buffer.poll(), ConcurrentIntRingBuffer.EMPTY);
	}

	@Test
	void whenPushingAndPollingElements_ThenGettingThoseElements() {
		final ConcurrentIntRingBuffer buffer = new ConcurrentIntRingBuffer(32);
		for (int i = 0; i < 10000; i++) {
			int value = FastMath.random(Integer.MAX_VALUE);
			buffer.add(value);
			assertEquals(buffer.poll(), value);
		}
	}

	@Test
	void whenOverflowing_ThenNotAddElementsOverCapacity() {
		final int size = 16;
		final ConcurrentIntRingBuffer buffer = new ConcurrentIntRingBuffer(size);
		for (int i = 0; i < size; i++)
			assertEquals(buffer.add(i + 346567), true);
		for (int i = 0; i < 1000; i++)
			assertEquals(buffer.add(i + 5674743), false);
		for (int i = 0; i < size; i++)
			assertEquals(buffer.poll(), i + 346567);
		assertEquals(buffer.poll(), ConcurrentIntRingBuffer.EMPTY);
	}

	private static final int TEST_SIZE = 10000;

	@Test
	void stressTestOnDifferentCapacities() throws Exception {
		stressTest(8, 8, 2, TEST_SIZE);
		stressTest(8, 8, 4, TEST_SIZE);
		stressTest(8, 8, 8, TEST_SIZE);
		stressTest(8, 8, 16, TEST_SIZE);
		stressTest(8, 8, 32, TEST_SIZE);
		stressTest(8, 8, 64, TEST_SIZE);
		stressTest(8, 8, 256, TEST_SIZE);
		stressTest(8, 8, 1024, TEST_SIZE);
	}

	@Test
	void stressTestOnDifferentConsumerPoducerSettings() throws Exception {
		stressTest(1, 32, 65536, TEST_SIZE);
		stressTest(32, 1, 65536, TEST_SIZE);
		stressTest(32, 32, 65536, TEST_SIZE);
	}

	private static void stressTest(final int producer, final int consumer, final int capacity,
			final int ValuesPerProducer) throws Exception {
		final int TOTAL = producer * ValuesPerProducer;

		ConcurrentIntRingBuffer queue = new ConcurrentIntRingBuffer(capacity);

		ExecutorService pool = Executors.newFixedThreadPool(producer + consumer);

		AtomicInteger nextValue = new AtomicInteger(1);
		AtomicInteger consumed = new AtomicInteger();

		Set<Integer> received = ConcurrentHashMap.newKeySet();

		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch finished = new CountDownLatch(producer + consumer);

		// Producers
		for (int i = 0; i < producer; i++) {
			pool.execute(() -> {
				try {
					start.await();
					while (true) {
						int value = nextValue.getAndIncrement();
						if (value > TOTAL)
							break;

						while (!queue.add(value)) {
							Thread.onSpinWait();
						}
					}

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					finished.countDown();
				}
			});
		}

		// Consumers
		for (int i = 0; i < consumer; i++) {
			pool.execute(() -> {
				try {
					start.await();
					while (consumed.get() < TOTAL) {
						int value = queue.poll();
						if (value == ConcurrentIntRingBuffer.EMPTY) {
							Thread.onSpinWait();
							continue;
						}

						assertTrue(received.add(value), "Duplicate value: " + value);
						consumed.incrementAndGet();
					}

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					finished.countDown();
				}
			});
		}

		start.countDown();
		assertTrue(finished.await(60, TimeUnit.SECONDS));
		pool.shutdownNow();
		assertEquals(TOTAL, consumed.get());
		assertEquals(TOTAL, received.size());

		for (int i = 1; i <= TOTAL; i++) {
			assertTrue(received.contains(i), "Missing value " + i);
		}
	}
}
