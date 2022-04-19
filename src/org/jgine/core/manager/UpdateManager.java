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

	public static void unregister(Scene scene) {}

	public static void update(Scene scene, Object identifier, Entity entity, Object value) {
		THREAD_LIST.get().add(new Update(scene, identifier, entity, value));
	}

	public final List<BiConsumer<Entity, Object>> getReciever(Scene scene, Object identifier) {
		return scene.getUpdateReciever(identifier);
	}

	public static Set<Object> getIdentifiers(Scene scene) {
		return scene.getUpdateIdentifiers();
	}

	public static class Update {

		public final Scene scene;
		public final Object identifier;
		public final Entity entity;
		public final Object value;

		public Update(Scene scene, Object identifier, Entity entity, Object value) {
			this.scene = scene;
			this.identifier = identifier;
			this.entity = entity;
			this.value = value;
		}

		public final void update() {
			for (BiConsumer<Entity, Object> reciever : scene.getUpdateReciever(identifier))
				reciever.accept(entity, value);
		}
	}
}
