package jgine.utils.scheduler;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jgine.utils.scheduler.TaskBuffer.Task;

/**
 * Manager for timed operations. Has functions to call a {@link Runnable} or
 * {@link Task} synchron or asynchron, after a given time, or in an interval.
 */
public class Scheduler {

	private static final TimeBuffer TIME_BUFFER = new TimeBuffer(4096);
	private static final TimeBuffer TIME_BUFFER_ASYNC = new TimeBuffer(4096);
	private static final TaskBuffer TASK_BUFFER = new TaskBuffer(4096);
	private static final TaskBuffer TASK_BUFFER_ASYNC = new TaskBuffer(4096);
	private static volatile Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();
	private static volatile Queue<Runnable> TASKS_PROC = new ConcurrentLinkedQueue<>();
	private static final VarHandle TASKS_HANDLE;

	static {
		try {
			TASKS_HANDLE = MethodHandles.lookup().findStaticVarHandle(Scheduler.class, "TASKS", Queue.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void update() {
		Service.update();
		TASKS_PROC.clear();
		Queue<Runnable> toRun = TASKS_PROC = (Queue<Runnable>) TASKS_HANDLE.getAndSet(TASKS_PROC);
		Runnable r;
		while ((r = toRun.poll()) != null) {
			r.run();
		}

		synchronized (TIME_BUFFER) {
			TIME_BUFFER.update(System.currentTimeMillis(), Runnable::run);
		}
		synchronized (TASK_BUFFER) {
			TASK_BUFFER.update(System.currentTimeMillis(), Runnable::run);
		}
		synchronized (TIME_BUFFER_ASYNC) {
			TIME_BUFFER_ASYNC.update(System.currentTimeMillis(), ThreadPool::execute);
		}
		synchronized (TASK_BUFFER_ASYNC) {
			TASK_BUFFER_ASYNC.update(System.currentTimeMillis(), ThreadPool::execute);
		}
	}

	public static void runTask(Runnable task) {
		TASKS.add(task);
	}

	public static void runTaskAsynchron(Runnable task) {
		ThreadPool.execute(task);
	}

	public static void runTaskLater(int timeInMills, Runnable task) {
		synchronized (TIME_BUFFER) {
			TIME_BUFFER.add(System.currentTimeMillis() + timeInMills, task);
		}
	}

	public static void runTaskLaterAsynchron(int timeInMills, Runnable task) {
		synchronized (TIME_BUFFER_ASYNC) {
			TIME_BUFFER_ASYNC.add(System.currentTimeMillis() + timeInMills, task);
		}
	}

	public static void runTaskTimer(int timeInMills, Task task) {
		synchronized (TASK_BUFFER) {
			task.tickTime = timeInMills;
			task.taskBuffer = TASK_BUFFER;
			TASK_BUFFER.add(System.currentTimeMillis() + timeInMills, task);
		}
	}

	public static void runTaskTimerAsynchron(int timeInMills, Task task) {
		synchronized (TASK_BUFFER_ASYNC) {
			task.tickTime = timeInMills;
			task.taskBuffer = TASK_BUFFER_ASYNC;
			TASK_BUFFER_ASYNC.add(System.currentTimeMillis() + timeInMills, task);
		}
	}
}
