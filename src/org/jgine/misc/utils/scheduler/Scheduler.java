package org.jgine.misc.utils.scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scheduler {

	private static final Queue<Runnable> SYNCHRON = new ConcurrentLinkedQueue<Runnable>();
	private static final TimeBuffer TIME_BUFFER = new TimeBuffer();

	public static void update() {
		for (Runnable task : SYNCHRON)
			task.run();
		SYNCHRON.clear();
		TIME_BUFFER.update(System.currentTimeMillis());
	}

	public static void runTaskSynchron(Runnable task) {
		SYNCHRON.add(task);
	}

	public static void runTaskAsynchron(Runnable task) {
		TaskExecutor.execute(task);
	}

	public static void runTaskLater(int timeInMills, Runnable task) {
		synchronized (TIME_BUFFER) {
			TIME_BUFFER.add(System.currentTimeMillis() + timeInMills, task);
		}
	}

	// public static void runTaskLaterAsynchron(int timeInMills, Runnable task) {
	// // TODO
	// }
	//
	// public static void runTaskTimer(int timeInMills, Runnable task) {
	// // TODO
	// }
	//
	// public static void runTaskTimerAsynchron(int timeInMills, Runnable task) {
	// // TODO
	// }
}
