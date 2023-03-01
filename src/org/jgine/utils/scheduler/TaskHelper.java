package org.jgine.utils.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.IntConsumer;

import org.jgine.utils.function.BiIntConsumer;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.options.Options;

/**
 * Helper class for parallel task execution. Uses {@link TaskExecutor}
 * internally.
 */
public class TaskHelper {

	public static void execute(int size, BiIntConsumer func) {
		if (Options.SYNCHRONIZED) {
			func.accept(0, size);
			return;
		}
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		int threadSize = TaskExecutor.getPoolSize();
		if (size < threadSize) {
			func.accept(0, size);
			return;
		}
		int updateSize = size / threadSize;
		int rest = size % threadSize;
		int i = 0;
		for (; i < threadSize - 1; i++) {
			int index = i * updateSize;
			tasks.add(TaskExecutor.submit(() -> func.accept(index, updateSize)));
		}
		int index = i * updateSize;
		tasks.add(TaskExecutor.submit(() -> func.accept(index, updateSize + rest)));
		waitTasks(tasks);
	}

	public static void execute(int size, IntConsumer func) {
		if (Options.SYNCHRONIZED) {
			for (int i = 0; i < size; i++)
				func.accept(i);
			return;
		}
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		int threadSize = TaskExecutor.getPoolSize();
		if (size < threadSize) {
			for (int i = 0; i < size; i++)
				func.accept(i);
			return;
		}
		int updateSize = size / threadSize;
		int rest = size % threadSize;
		int i = 0;
		for (; i < threadSize - 1; i++) {
			int index = i * updateSize;
			tasks.add(TaskExecutor.submit(() -> {
				int loopSize = index + updateSize;
				for (int j = index; j < loopSize; j++)
					func.accept(j);
			}));
		}
		int index = i * updateSize;
		tasks.add(TaskExecutor.submit(() -> {
			int loopSize = index + updateSize + rest;
			for (int j = index; j < loopSize; j++)
				func.accept(j);
		}));
		waitTasks(tasks);
	}

	private static void waitTasks(Iterable<Future<?>> tasks) {
		try {
			for (Future<?> future : tasks)
				future.get();
		} catch (InterruptedException | ExecutionException e) {
			Logger.err("TaskManager: Error on waiting for tasks!", e);
		}
	}
}
