package org.jgine.misc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Benchmark {

	private static final Map<Object, BenchmarkData> BENCHMARKS = new ConcurrentHashMap<Object, BenchmarkData>();

	public static BenchmarkData get(Object identifier) {
		BenchmarkData data = BENCHMARKS.get(identifier);
		if (data == null)
			BENCHMARKS.put(identifier, data = new BenchmarkData());
		return data;
	}

	public static void start(Object identifier) {
		get(identifier).startTimer();
	}

	public static void stop(Object identifier) {
		get(identifier).stopTimer();
	}

	public static void clear(Object identifier) {
		get(identifier).clear();
	}

	public static long benchmark(Runnable func) {
		BenchmarkData benchmark = new BenchmarkData();
		benchmark.startTimer();
		func.run();
		benchmark.stopTimer();
		return benchmark.getTime();
	}

	public static class BenchmarkData {

		private long startTime;
		private int size;
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
			long deltaTime = java.lang.Math.abs(System.nanoTime() - startTime);
			if (deltaTime < best)
				best = deltaTime;
			if (deltaTime > worst)
				worst = deltaTime;
			time += deltaTime;
			size++;
			startTime = 0;
		}

		public void clear() {
			size = 0;
			time = 0;
			best = Long.MAX_VALUE;
			worst = 0;
		}

		public long getTime() {
			return time / size;
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
	}
}
