package jgine.utils.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jgine.utils.Environment;
import jgine.utils.Logger;
import jgine.utils.Options;

/**
 * Helper class for parallel task execution. Uses {@link ThreadPoolExecutor}
 * internally.
 */
public class ThreadPool {

	private static final ThreadPoolExecutor THREAD_POOL;

	static {
		THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(Environment.availableProcessors(),
				new WorkerThreadFactory());
		THREAD_POOL.prestartAllCoreThreads();
	}

	public static void execute(Runnable task) {
		if (Options.SYNCHRONIZED)
			task.run();
		else
			THREAD_POOL.execute(task);
	}

	public static Future<?> submit(Runnable task) {
		if (Options.SYNCHRONIZED) {
			task.run();
			return (DummyFuture<?>) () -> null;
		}
		return THREAD_POOL.submit(task);
	}

	public static <T> Future<T> submit(Runnable task, T result) {
		if (Options.SYNCHRONIZED) {
			task.run();
			return (DummyFuture<T>) () -> result;
		}
		return THREAD_POOL.submit(task, result);
	}

	public static <T> Future<T> submit(Callable<T> task) {
		if (Options.SYNCHRONIZED) {
			try {
				T result = task.call();
				return (DummyFuture<T>) () -> result;
			} catch (Exception e) {
				Logger.err("ThreadPool: Error in submit [" + task + "]", e);
				return (DummyFuture<T>) () -> null;
			}
		}
		return THREAD_POOL.submit(task);
	}

	public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		if (Options.SYNCHRONIZED) {
			List<Future<T>> result = new ArrayList<Future<T>>(tasks.size());
			for (Callable<T> task : tasks) {
				try {
					T t = task.call();
					result.add((DummyFuture<T>) () -> t);
				} catch (Exception e) {
					Logger.err("ThreadPool: Error in invokeAll [" + task + "]", e);
					result.add((DummyFuture<T>) () -> null);
				}
			}
			return result;
		}
		return THREAD_POOL.invokeAll(tasks);
	}

	public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, int timeout, TimeUnit unit)
			throws InterruptedException {
		if (Options.SYNCHRONIZED)
			return invokeAll(tasks);
		return THREAD_POOL.invokeAll(tasks, timeout, unit);
	}

	public static <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		if (Options.SYNCHRONIZED) {
			T result = null;
			for (Callable<T> task : tasks) {
				try {
					result = task.call();
				} catch (Exception ignore) {
					continue;
				}
				return result;
			}
		}
		return THREAD_POOL.invokeAny(tasks);
	}

	public static <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (Options.SYNCHRONIZED)
			return invokeAny(tasks);
		return THREAD_POOL.invokeAny(tasks, timeout, unit);
	}

	public static boolean remove(Runnable task) {
		return THREAD_POOL.remove(task);
	}

	public static void shutdown() {
		THREAD_POOL.shutdown();
	}

	public static List<Runnable> shutdownNow() {
		return THREAD_POOL.shutdownNow();
	}

	public static boolean isShutdown() {
		return THREAD_POOL.isShutdown();
	}

	public static boolean isTerminated() {
		return THREAD_POOL.isTerminated();
	}

	public static boolean isTerminating() {
		return THREAD_POOL.isTerminating();
	}

	public static int getPoolSize() {
		return THREAD_POOL.getMaximumPoolSize();
	}

	public static int getCurrentThreadSize() {
		return THREAD_POOL.getPoolSize();
	}

	public static int getActiveThreadSize() {
		return THREAD_POOL.getActiveCount();
	}

	public static long getTaskSize() {
		return THREAD_POOL.getTaskCount();
	}

	public static BlockingQueue<Runnable> getQueue() {
		return THREAD_POOL.getQueue();
	}

	public static ThreadPoolExecutor getExecutor() {
		return THREAD_POOL;
	}

	private static class WorkerThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(false);
			thread.setPriority(Thread.MAX_PRIORITY);
			return thread;
		}
	}

	@FunctionalInterface
	private static interface DummyFuture<T> extends Future<T> {

		@Override
		public default boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public default boolean isCancelled() {
			return false;
		}

		@Override
		public default boolean isDone() {
			return true;
		}

		@Override
		public default T get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return get();
		}
	}
}
