package org.jgine.misc.utils.scheduler;

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

import org.jgine.core.manager.PlatformManager;

public class TaskExecutor {

	// TODO: Bind thread to core!

	private static final ThreadPoolExecutor THREAD_POOL;

	static {
		THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(PlatformManager.getProcessorsSize(),
				new WorkerThreadFactory());
		THREAD_POOL.prestartAllCoreThreads();
	}

	public static void execute(Runnable task) {
		THREAD_POOL.execute(task);
	}

	public static Future<?> submit(Runnable task) {
		return THREAD_POOL.submit(task);
	}

	public static <T> Future<T> submit(Runnable task, T result) {
		return THREAD_POOL.submit(task, result);
	}

	public static <T> Future<T> submit(Callable<T> task) {
		return THREAD_POOL.submit(task);
	}

	public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return THREAD_POOL.invokeAll(tasks);
	}

	public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, int timeout, TimeUnit unit)
			throws InterruptedException {
		return THREAD_POOL.invokeAll(tasks, timeout, unit);
	}

	public static <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return THREAD_POOL.invokeAny(tasks);
	}

	public static <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
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
}
