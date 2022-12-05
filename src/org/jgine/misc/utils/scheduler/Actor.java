package org.jgine.misc.utils.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * from https://github.com/strehler/workshop-jbcn
 */
public interface Actor {

	public interface Effect<T> {
		Behavior<T> transition(Behavior<T> next);
	}

	public interface Behavior<T> {
		Effect<T> receive(T o);
	}

	public interface Address<T> {
		Address<T> tell(T msg);
	}

	public static <T> Effect<T> Become(Behavior<T> next) {
		return current -> next;
	}

	public static <T> Effect<T> Stay() {
		return current -> current;
	}

	public static <T> Address<T> of(Function<Address<T>, Behavior<T>> initial) {
		return new AtomicRunnableAddress<T>(initial);
	}

	public class AtomicRunnableAddress<T> implements Address<T>, Runnable {

		private final AtomicBoolean on;
		private Behavior<T> behavior;
		private final ConcurrentLinkedQueue<T> mbox;

		private AtomicRunnableAddress(Function<Address<T>, Behavior<T>> initial) {
			this.on = new AtomicBoolean(false);
			this.behavior = initial.apply(this);
			this.mbox = new ConcurrentLinkedQueue<>();
		}

		// Enqueue the message onto the mailbox and try to schedule for execution
		// Switch ourselves off, and then see if we should be rescheduled for execution
		@Override
		public Address<T> tell(T msg) {
			mbox.offer(msg);
			async();
			return this;
		}

		@Override
		public void run() {
			try {
				if (on.get()) {
					T m = mbox.poll();
					if (m != null) {
						Effect<T> effect = behavior.receive(m);
						behavior = effect.transition(behavior);
					}
				}
			} finally {
				on.set(false);
				async();
			}
		}

		// If there's something to process, and we're not already scheduled
		private void async() {
			if (!mbox.isEmpty() && on.compareAndSet(false, true)) {
				// Schedule to run on the Executor and back out on failure
				try {
					TaskExecutor.execute(this);
				} catch (Throwable t) {
					on.set(false);
					throw t;
				}
			}
		}
	}
}
