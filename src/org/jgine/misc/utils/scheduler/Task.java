package org.jgine.misc.utils.scheduler;

public abstract class Task implements Runnable {

	int tickTime;
	TaskBuffer taskBuffer;
	private boolean canceled;

	public void cancel() {
		synchronized (taskBuffer) {
			taskBuffer.remove(this);
		}
		canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public int getTickTime() {
		return tickTime;
	}
}
