package jgine.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import jgine.core.Scene;

public class SystemMapTest {

	@SuppressWarnings("rawtypes")
	class Sys extends EngineSystem {

		public Sys(String name) {
			super(name);
		}

		@Override
		public SystemScene createScene(Scene scene) {
			return null;
		}

		@Override
		public SystemObject load(Map data) {
			return null;
		}
	}

	class Obj implements SystemObject {

		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public void load(Map<String, Object> data) {
		}

		@Override
		public void save(Map<String, Object> data) {
		}

		@Override
		public void load(DataInput in) throws IOException {
		}

		@Override
		public void save(DataOutput out) throws IOException {
		}

		@Override
		public int system() {
			return 0;
		}
	}

	@Test
	void singleThreadAddGetRemove() {
		SystemMap es = new SystemMap(null);
		int a = new Sys("0").id;
		int b = new Sys("1").id;
		Obj a0 = new Obj();
		Obj b0 = new Obj();
		Obj b1 = new Obj();

		es.map(a, a0);
		es.map(b, b0);
		es.map(b, b1);

		assertSame(a0, es.get(a));
		assertSame(b0, es.get(b));

		assertNotNull(es.get(a, 0));
		assertNull(es.get(a, 1));

		assertTrue(es.unmap(a0) != -1);
		assertNull(es.get(a));
		assertNull(es.get(a, 0));

		assertSame(b0, es.get(b));
		assertSame(b0, es.get(b, 0));
		assertSame(b1, es.get(b, 1));
	}

	@Test
	void concurrentAddAndRead() throws Exception {
		SystemMap es = new SystemMap(null);
		int a = new Sys("00").id;
		ExecutorService pool = Executors.newFixedThreadPool(8);

		int writers = 4;
		int readers = 4;
		int perWriter = 50_000;

		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(writers + readers);

		// writers
		for (int w = 0; w < writers; w++) {
			pool.submit(() -> {
				try {
					start.await();
					for (int i = 0; i < perWriter; i++) {
						es.map(a, new Obj());
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			});
		}

		// readers
		for (int r = 0; r < readers; r++) {
			pool.submit(() -> {
				try {
					start.await();
					for (int i = 0; i < perWriter * writers; i++) {
						es.get(i % writers, i);
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			});
		}

		start.countDown();
		done.await();
		pool.shutdown();

		assertEquals(writers * perWriter, es.size());
	}

	@Test
	void concurrentGrowStress() throws Exception {
		SystemMap es = new SystemMap(null);
		int a = new Sys("000").id;
		ExecutorService pool = Executors.newFixedThreadPool(8);

		int threads = 8;
		int addsPerThread = 20_000;

		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);

		for (int t = 0; t < threads; t++) {
			pool.submit(() -> {
				try {
					start.await();
					for (int i = 0; i < addsPerThread; i++) {
						es.map(a, new Obj());
					}
				} catch (InterruptedException ignored) {
				} finally {
					done.countDown();
				}
			});
		}

		start.countDown();
		done.await();
		pool.shutdown();

		assertEquals(threads * addsPerThread, es.size());
	}

	@Test
	void tombstoneBehavior() {
		SystemMap es = new SystemMap(null);
		int a = new Sys("0000").id;
		int b = new Sys("0001").id;
		int c = new Sys("0002").id;
		Obj a0 = new Obj();
		Obj b0 = new Obj();
		Obj c0 = new Obj();

		es.map(a, a0);
		es.map(b, b0);
		es.map(c, c0);

		assertTrue(es.unmap(b, (_) -> {
		}));

		assertSame(a0, es.get(a));
		assertNull(es.get(b));
		assertSame(c0, es.get(c));
	}

	@Test
	void compactRemovesTombstones() {
		SystemMap es = new SystemMap(null);
		int a = new Sys("00000").id;
		int b = new Sys("00001").id;
		int c = new Sys("00002").id;
		Obj a0 = new Obj();
		Obj b0 = new Obj();
		Obj c0 = new Obj();

		es.map(a, a0);
		es.map(b, b0);
		es.map(c, c0);

		es.unmap(b0);

		// EXCLUSIVE PHASE
		es.compact();

		assertEquals(2, es.size());
		assertSame(a0, es.get(a));
		assertNull(es.get(b));
		assertSame(c0, es.get(c));
	}
}
