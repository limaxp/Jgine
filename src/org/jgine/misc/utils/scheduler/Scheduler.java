package org.jgine.misc.utils.scheduler;

public class Scheduler {

	protected static final int DEFAULT_CAPACITY = 4096;

	private static final Runnable[] SYNCHRON_ARRAY = new Runnable[DEFAULT_CAPACITY];
	private static int synchronSize;
	private static final TimeBuffer<Runnable> TIME_BUFFER = new TimeBuffer<Runnable>(DEFAULT_CAPACITY);
	private static final TimeBuffer<Runnable> TIME_BUFFER_ASYNC = new TimeBuffer<Runnable>(DEFAULT_CAPACITY);
	private static final TaskBuffer TASK_BUFFER = new TaskBuffer(DEFAULT_CAPACITY);
	private static final TaskBuffer TASK_BUFFER_ASYNC = new TaskBuffer(DEFAULT_CAPACITY);

	public static void update() {
		synchronized (SYNCHRON_ARRAY) {
			for (int i = 0; i < synchronSize; i++)
				SYNCHRON_ARRAY[i].run();
			synchronSize = 0;
		}

		long time = System.currentTimeMillis();
		synchronized (TIME_BUFFER) {
			TIME_BUFFER.update(time, Runnable::run);
		}
		synchronized (TIME_BUFFER_ASYNC) {
			TIME_BUFFER_ASYNC.update(time, TaskExecutor::execute);
		}
		synchronized (TASK_BUFFER) {
			TASK_BUFFER.update(time, Runnable::run);
		}
		synchronized (TASK_BUFFER_ASYNC) {
			TASK_BUFFER_ASYNC.update(time, TaskExecutor::execute);
		}
	}

	public static void runTaskSynchron(Runnable task) {
		synchronized (SYNCHRON_ARRAY) {
			SYNCHRON_ARRAY[synchronSize++] = task;
		}
	}

	public static void runTaskAsynchron(Runnable task) {
		TaskExecutor.execute(task);
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
