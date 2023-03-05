package org.jgine.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for benchmarking per identifier.
 */
public class Benchmark {

	private static final Map<Object, BenchmarkData> BENCHMARKS = new ConcurrentHashMap<Object, BenchmarkData>();

	public static BenchmarkData get(Object identifier) {
		BenchmarkData data = BENCHMARKS.get(identifier);
		if (data == null)
			BENCHMARKS.put(identifier, data = new BenchmarkData());
		return data;
	}

	public static BenchmarkData start(Object identifier) {
		BenchmarkData benchmark = get(identifier);
		benchmark.startTimer();
		return benchmark;
	}

	public static BenchmarkData stop(Object identifier) {
		BenchmarkData benchmark = get(identifier);
		benchmark.stopTimer();
		return benchmark;
	}

	public static BenchmarkData clear(Object identifier) {
		BenchmarkData benchmark = get(identifier);
		benchmark.clear();
		return benchmark;
	}

	public static BenchmarkData benchmark(Object identifier, Runnable func) {
		BenchmarkData benchmark = get(identifier);
		benchmark.startTimer();
		func.run();
		benchmark.stopTimer();
		return benchmark;
	}

	public static long benchmark(Runnable func) {
		BenchmarkData benchmark = new BenchmarkData();
		benchmark.startTimer();
		func.run();
		benchmark.stopTimer();
		return benchmark.getAverage();
	}

	/**
	 * Container for {@link Benchmark} data. Tracks average, best, worst time and
	 * test count.
	 */
	public static class BenchmarkData {

		private long startTime;
		private int count;
		private long time;
		private long best = Long.MAX_VALUE;
		private long worst;

		protected void startTimer() {
			if (!isTiming())
				startTime = System.nanoTime();
		}

		protected void stopTimer() {
			if (!isTiming())
				return;
			long deltaTime = System.nanoTime() - startTime;
			if (deltaTime == 0) {
				startTime = 0;
				return;
			}
			if (deltaTime < best)
				best = deltaTime;
			if (deltaTime > worst)
				worst = deltaTime;
			time += deltaTime;
			count++;
			startTime = 0;
		}

		public void clear() {
			count = 0;
			time = 0;
			best = Long.MAX_VALUE;
			worst = 0;
		}

		public long getAverage() {
			return time / count;
		}

		public long getBest() {
			return best;
		}

		public long getWorst() {
			return worst;
		}

		public boolean isTiming() {
			return startTime != 0;
		}

		public int getCount() {
			return count;
		}
	}
}
