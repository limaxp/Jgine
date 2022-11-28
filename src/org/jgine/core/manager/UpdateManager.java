package org.jgine.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.misc.utils.options.Options;
import org.jgine.misc.utils.scheduler.TaskExecutor;

public class UpdateManager {

	public static final Object TRANSFORM_POSITION_IDENTIFIER = new Object();
	public static final Object TRANSFORM_SCALE_IDENTIFIER = new Object();
	public static final Object PHYSIC_POSITION_IDENTIFIER = new Object();

	private static final int QUEUE_SIZE = 100000;
	@SuppressWarnings("unchecked") // extra slot so start thread can do updates
	private static final List<Update>[] THREAD_LISTS = new List[TaskExecutor.getPoolSize() + 1];
	private static int listSize;
	private static final ThreadLocal<List<Update>> THREAD_LIST = new ThreadLocal<List<Update>>() {

		@Override
		protected List<Update> initialValue() {
			List<Update> list = new ArrayList<Update>(QUEUE_SIZE);
			THREAD_LISTS[listSize++] = list;
			return list;
		}
	};
	private static final List<Future<?>> UPDATE_TASKS = new ArrayList<Future<?>>();

	public static void distributeChanges() {
		if (Options.SYNCHRONIZED) {
			for (int i = 0; i < listSize; i++)
				distributeChanges(THREAD_LISTS[i]);
			return;
		}

		for (int i = 0; i < listSize; i++) {
			int treadIndex = i;
			UPDATE_TASKS.add(TaskExecutor.submit(() -> distributeChanges(THREAD_LISTS[treadIndex])));
		}
		try {
			for (Future<?> future : UPDATE_TASKS)
				future.get();
		} catch (InterruptedException | ExecutionException e) {
			Logger.err("UpdateManager: Error on running update tasks!", e);
		}
		UPDATE_TASKS.clear();
	}

	private static void distributeChanges(List<Update> list) {
		for (Update update : list)
			update.update();
		list.clear();
	}

	public static void register(Scene scene, Object identifier, BiConsumer<Entity, Object> func) {
		scene.getUpdateReciever(identifier).add(func);
	}

	public static void unregister(Scene scene, Object identifier, BiConsumer<Entity, Object> func) {
		scene.getUpdateReciever(identifier).remove(func);
	}

	public static void unregister(Scene scene) {
	}

	public static void update(Entity entity, Object identifier, Object value) {
		THREAD_LIST.get().add(new Update(entity, identifier, value));
	}

	public final List<BiConsumer<Entity, Object>> getReciever(Scene scene, Object identifier) {
		return scene.getUpdateReciever(identifier);
	}

	public static Set<Object> getIdentifiers(Scene scene) {
		return scene.getUpdateIdentifiers();
	}

	public static class Update {

		public final Entity entity;
		public final Object identifier;
		public final Object value;

		public Update(Entity entity, Object identifier, Object value) {
			this.entity = entity;
			this.identifier = identifier;
			this.value = value;
		}

		public final void update() {
			for (BiConsumer<Entity, Object> reciever : entity.scene.getUpdateReciever(identifier))
				reciever.accept(entity, value);
		}
	}
}
