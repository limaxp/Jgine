package jgine.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IdGeneratorTest {

	@Test
	void whenGeneratingTillFull_GettingThoseIds() {
		int size = 1024;
		IdGenerator gen = new IdGenerator(size);
		for (int i = 0; i < size; i++)
			assertEquals(gen.generate(), i);
		assertThrows(IndexOutOfBoundsException.class, gen::generate);
	}

	@Test
	void whenGeneratingAndFreeing_GenerationOverflowsCorrectly() {
		int size = 256;
		IdGenerator gen = new IdGenerator(size);
		for (int i = 0; i < size * 4; i++) {
			int id = gen.generate();
			assertEquals(id, IdGenerator.id(0, i % 256));
			gen.free(id);
		}
	}

	@Test
	void staleIdsEventuallyDie() {
		IdGenerator generator = new IdGenerator(8);
		int original = generator.generate();
		generator.free(original);
		boolean reused = false;
		for (int i = 0; i < 100; i++) {
			int id = generator.generate();
			if (IdGenerator.index(id) == IdGenerator.index(original)) {
				reused = true;
				assertNotEquals(original, id);
				assertFalse(generator.isAlive(original));
				assertTrue(generator.isAlive(id));
				break;
			}
			generator.free(id);
		}
		assertTrue(reused);
	}

	@Test
	void stressTest() throws Exception {

		final int CAPACITY = 4096;
		final int THREADS = 8;
		final int ITERATIONS = 200_000;

		IdGenerator generator = new IdGenerator(CAPACITY);
		// Tracks which indices are currently owned.
		AtomicIntegerArray owners = new AtomicIntegerArray(CAPACITY);
		ExecutorService pool = Executors.newFixedThreadPool(THREADS);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(THREADS);
		AtomicBoolean failed = new AtomicBoolean(false);

		for (int t = 0; t < THREADS; t++) {
			final int threadId = t + 1;
			pool.execute(() -> {

				try {
					start.await();
					ThreadLocalRandom rnd = ThreadLocalRandom.current();
					for (int i = 0; i < ITERATIONS && !failed.get(); i++) {
						int id = generator.generate();
						int index = IdGenerator.index(id);

						// Index must never be owned twice simultaneously.
						if (!owners.compareAndSet(index, 0, threadId)) {
							failed.set(true);
							fail("Duplicate live index: " + index);
						}
						assertTrue(generator.isAlive(id));

						// Random scheduling noise.
						if (rnd.nextInt(16) == 0)
							Thread.yield();
						owners.set(index, 0);
						generator.free(id);
					}

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			});
		}

		start.countDown();
		assertTrue(done.await(60, TimeUnit.SECONDS));
		pool.shutdownNow();
		assertFalse(failed.get());
	}
}
