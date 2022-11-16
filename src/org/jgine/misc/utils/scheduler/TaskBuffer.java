package org.jgine.misc.utils.scheduler;

import java.util.function.Consumer;

public class TaskBuffer extends TimeBuffer<Task> {

	public TaskBuffer(int capacity) {
		super(capacity);
	}

	@Override
	public void update(long time, Consumer<Task> func) {
		if (size == 0)
			return;
		if (time < timeArray[0])
			return;
		if (time >= timeArray[size - 1]) {
			for (int i = 0; i < size; i++) {
				Task task = (Task) objectArray[i];
				func.accept(task);
				timeArray[i] = time + task.tickTime;
			}
			return;
		}
		int index = 0;
		for (; index < size; index++) {
			if (timeArray[index] > time)
				break;
			func.accept((Task) objectArray[index]);
		}
		Object[] temp = new Object[index];
		System.arraycopy(objectArray, 0, temp, 0, index);
		System.arraycopy(timeArray, index, timeArray, 0, size - index);
		System.arraycopy(objectArray, index, objectArray, 0, size - index);
		for (int i = 0; i < index; i++) {
			int j = index + i;
			Task taks = (Task) temp[i];
			timeArray[j] = time + taks.tickTime;
			objectArray[j] = taks;
		}
	}
}
