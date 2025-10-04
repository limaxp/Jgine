package org.jgine.utils.collection.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ObjectPool<E> {

	private final BlockingQueue<E> pool;
	private final ReentrantLock lock = new ReentrantLock();
	private int createdObjects = 0;
	private int size;

	protected ObjectPool(int size) {
		this(size, false);
	}

	protected ObjectPool(int size, boolean dynamicCreation) {
		// Enable the fairness; otherwise, some threads
		// may wait forever.
		pool = new ArrayBlockingQueue<>(size, true);
		this.size = size;
		if (!dynamicCreation) {
			lock.lock();
		}
	}

	public E acquire() throws Exception {
		if (!lock.isLocked()) {
			if (lock.tryLock()) {
				try {
					++createdObjects;
					return createObject();
				} finally {
					if (createdObjects < size)
						lock.unlock();
				}
			}
		}
		return pool.take();
	}

	public void recycle(E resource) {
		// Will throws Exception when the queue is full,
		// but it should never happen.
		pool.add(resource);
	}

	public void createPool() {
		if (lock.isLocked()) {
			for (int i = 0; i < size; ++i) {
				pool.add(createObject());
				createdObjects++;
			}
		}
	}

	protected abstract E createObject();
}