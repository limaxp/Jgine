package org.jgine.misc.utils.scheduler;

public abstract class Task implements Runnable {

	int tickTime;
	TaskBuffer taskBuffer;

	public void cancel() {
		synchronized (taskBuffer) {
			taskBuffer.remove(this);
		}
	}

	public int getTickTime() {
		return tickTime;
	}
}
