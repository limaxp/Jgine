package org.jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.system.EngineSystem;

/**
 * Defines an update or render order for a {@link Scene}. Use add() methods to
 * add a system and its required {@link EngineSystem}<code>s</code>.
 */
public class UpdateOrder {

	private List<EngineSystem<?, ?>> start;
	private List<EngineSystem<?, ?>>[] before;
	private List<EngineSystem<?, ?>>[] after;
	private int size;

	@SuppressWarnings("unchecked")
	public UpdateOrder() {
		start = new IdentityArrayList<EngineSystem<?, ?>>();
		int size = EngineSystem.size();
		before = new List[size];
		after = new List[size];
		for (int i = 0; i < size; i++) {
			before[i] = new IdentityArrayList<EngineSystem<?, ?>>();
			after[i] = new IdentityArrayList<EngineSystem<?, ?>>();
		}
	}

	public void add(EngineSystem<?, ?> system, Collection<EngineSystem<?, ?>> before) {
		int systemId = system.id;
		for (EngineSystem<?, ?> b : before) {
			int beforeId = b.id;
			this.before[systemId].add(b);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem<?, ?> system, EngineSystem<?, ?>... before) {
		int systemId = system.id;
		for (int i = 0; i < before.length; i++) {
			EngineSystem<?, ?> b = before[i];
			int beforeId = b.id;
			this.before[systemId].add(b);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem<?, ?> system, EngineSystem<?, ?> before) {
		this.before[system.id].add(before);
		this.after[before.id].add(system);
		size++;
	}

	public void add(EngineSystem<?, ?> system) {
		start.add(system);
		size++;
	}

	public List<EngineSystem<?, ?>> getStart() {
		return start;
	}

	public List<EngineSystem<?, ?>> getBefore(EngineSystem<?, ?> system) {
		return before[system.id];
	}

	public List<EngineSystem<?, ?>> getAfter(EngineSystem<?, ?> system) {
		return after[system.id];
	}

	public int size() {
		return size;
	}

	public void load(DataInput in) throws IOException {
		int startSize = in.readInt();
		for (int i = 0; i < startSize; i++)
			start.add(EngineSystem.get(in.readInt()));

		int dataSize = in.readInt();
		for (int i = 0; i < dataSize; i++) {
			List<EngineSystem<?, ?>> beforeList = before[i];
			int beforeSize = in.readInt();
			for (int j = 0; j < beforeSize; j++)
				beforeList.add(EngineSystem.get(in.readInt()));

			List<EngineSystem<?, ?>> afterList = after[i];
			int afterSize = in.readInt();
			for (int j = 0; j < afterSize; j++)
				afterList.add(EngineSystem.get(in.readInt()));
		}
		size = in.readInt();
	}

	public void save(DataOutput out) throws IOException {
		int startSize = start.size();
		out.writeInt(startSize);
		for (int i = 0; i < startSize; i++)
			out.writeInt(start.get(i).id);

		int dataSize = before.length;
		out.writeInt(dataSize);
		for (int i = 0; i < dataSize; i++) {
			List<EngineSystem<?, ?>> beforeList = before[i];
			int beforeSize = beforeList.size();
			out.writeInt(beforeSize);
			for (int j = 0; j < beforeSize; j++)
				out.writeInt(beforeList.get(j).id);

			List<EngineSystem<?, ?>> afterList = after[i];
			int afterSize = afterList.size();
			out.writeInt(afterSize);
			for (int j = 0; j < afterSize; j++)
				out.writeInt(afterList.get(j).id);
		}
		out.writeInt(size);
	}

	public static class SynchronizedUpdateTask {

		protected final Scene scene;
		protected final UpdateOrder order;
		protected final AtomicIntegerArray flags;
		protected final float dt;

		public SynchronizedUpdateTask(Scene scene, UpdateOrder order, float dt) {
			this.scene = scene;
			this.order = order;
			flags = new AtomicIntegerArray(EngineSystem.size());
			this.dt = dt;
		}

		public void start() {
			List<EngineSystem<?, ?>> start = order.getStart();
			for (int i = 0; i < start.size(); i++)
				update(start.get(i));
		}

		protected void update(EngineSystem<?, ?> system) {
			scene.getSystem(system).update(dt);
			check(system);
		}

		protected final void check(EngineSystem<?, ?> system) {
			flags.set(system.id, 1);
			for (EngineSystem<?, ?> after : order.getAfter(system))
				subCheck(after);
		}

		private final void subCheck(EngineSystem<?, ?> system) {
			if (flags.get(system.id) == 1)
				return;

			for (EngineSystem<?, ?> before : order.getBefore(system))
				if (flags.get(before.id) == 0)
					return;
			update(system);
		}
	}

	public static class SynchronizedRenderTask extends SynchronizedUpdateTask {

		public SynchronizedRenderTask(Scene scene, UpdateOrder order, float dt) {
			super(scene, order, dt);
		}

		@Override
		protected void update(EngineSystem<?, ?> system) {
			scene.getSystem(system).render(dt);
			check(system);
		}
	}

	public static class UpdateTask extends SynchronizedUpdateTask {

		protected AtomicInteger amount;
		protected Thread thread;

		public UpdateTask(Scene scene, UpdateOrder order, float dt) {
			super(scene, order, dt);
			amount = new AtomicInteger(order.size());
			thread = Thread.currentThread();
		}

		@Override
		public void start() {
			super.start();
			while (amount.get() > 1) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
		}

		@Override
		protected void update(EngineSystem<?, ?> system) {
			scene.getSystem(system).update(dt);
			amount.decrementAndGet();
			check(system);

//			ThreadPool.execute(() -> {
//				scene.getSystem(system).update(dt);
//				if (amount.decrementAndGet() <= 0)
//					thread.interrupt();
//				else
//					check(system);
//			});
		}
	}

	public static class RenderTask extends UpdateTask {

		public RenderTask(Scene scene, UpdateOrder order, float dt) {
			super(scene, order, dt);
		}

		@Override
		protected void update(EngineSystem<?, ?> system) {
			scene.getSystem(system).render(dt);
			amount.decrementAndGet();
			check(system);

//			ThreadPool.execute(() -> {
//				scene.getSystem(system).render(dt);
//				amount.decrementAndGet();
//				if (amount.decrementAndGet() <= 0)
//					thread.interrupt();
//				else
//					check(system);
//			});
		}
	}
}
