package org.jgine.utils.scheduler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import org.jgine.utils.options.Options;

public class Job {

	public static void simple(int size, IntConsumer func, Runnable finishFunc) {
		if (Options.SYNCHRONIZED) {
			for (int i = 0; i < size; i++)
				func.accept(i);
			finishFunc.run();
			return;
		}

		AtomicInteger atomicSize = new AtomicInteger(size);
		for (int i = 0; i < size; i++) {
			int index = i;
			ThreadPool.execute(() -> {
				func.accept(index);
				if (atomicSize.decrementAndGet() <= 0)
					finishFunc.run();
			});
		}
	}

	public static void region(int size, IntConsumer func, Runnable finishFunc) {
		region(size, ThreadPool.getPoolSize(), func, finishFunc);
	}

	public static void region(int size, int regionSize, IntConsumer func, Runnable finishFunc) {
		if (Options.SYNCHRONIZED) {
			for (int i = 0; i < size; i++)
				func.accept(i);
			finishFunc.run();
			return;
		}

		if (size < regionSize) {
			for (int i = 0; i < size; i++)
				func.accept(i);
			finishFunc.run();
			return;
		}

		AtomicInteger atomicSize = new AtomicInteger(regionSize);
		int updateSize = size / regionSize;
		int rest = size % regionSize;
		int i = 0;
		for (; i < regionSize - 1; i++) {
			int index = i * updateSize;
			ThreadPool.execute(() -> {
				for (int j = 0; j < updateSize; j++)
					func.accept(index + j);
				if (atomicSize.decrementAndGet() <= 0)
					finishFunc.run();
			});
		}
		int index = i * updateSize;
		ThreadPool.execute(() -> {
			for (int j = 0; j < updateSize + rest; j++)
				func.accept(index + j);
			if (atomicSize.decrementAndGet() <= 0)
				finishFunc.run();
		});
	}
}
