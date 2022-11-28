package org.jgine.misc.utils.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.UpdateOrder;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.system.SystemScene;

public class SceneLoader {

	public static Scene load(File file) {
		try (DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			return load(is);
		} catch (FileNotFoundException e) {
			Logger.err("SceneLoader: File '" + file.getPath() + "' not found", e);
		} catch (IOException e) {
			Logger.err("SceneLoader: Error loading file '" + file.getPath() + "'", e);
		}
		return null;
	}

	public static void write(Scene scene, File file) {
		try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			write(scene, os);
		} catch (FileNotFoundException e) {
			Logger.err("SceneLoader: File '" + file.getPath() + "' not found", e);
		} catch (IOException e) {
			Logger.err("SceneLoader: Error writing file '" + file.getPath() + "'", e);
		}
	}

	// TODO transform calculates Matrix 2 times on load!

	public static Scene load(DataInput in) throws IOException {
		String name = in.readUTF();
		Scene scene = Engine.getInstance().createScene(name);

		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++)
			scene.addSystem(in.readInt());

		int entitySize = in.readInt();
		for (int i = 0; i < entitySize; i++)
			new Entity(scene).load(in);

		boolean hasUpdateOrder = in.readBoolean();
		if (hasUpdateOrder) {
			UpdateOrder updateOrder = new UpdateOrder();
			updateOrder.load(in);
			scene.setUpdateOrder(updateOrder);
		}
		boolean hasRenderOrder = in.readBoolean();
		if (hasRenderOrder) {
			UpdateOrder renderOrder = new UpdateOrder();
			renderOrder.load(in);
			scene.setRenderOrder(renderOrder);
		}
		// TODO spacePartitioning
		return scene;
	}

	public static void write(Scene scene, DataOutput out) throws IOException {
		// TODO write must be synchronized! Scheduler.runTaskSynchron()
		out.writeUTF(scene.name);

		Collection<SystemScene<?, ?>> systems = scene.getSystems();
		out.writeInt(systems.size());
		for (SystemScene<?, ?> systemScene : systems)
			out.writeInt(systemScene.system.getId());

		List<Entity> entities = scene.getEntities();
		out.writeInt(entities.size());
		for (Entity entity : entities)
			entity.save(out);

		UpdateOrder updateOrder = scene.getUpdateOrder();
		if (updateOrder != null) {
			out.writeBoolean(true);
			updateOrder.save(out);
		} else
			out.writeBoolean(false);
		UpdateOrder renderOrder = scene.getRenderOrder();
		if (renderOrder != null) {
			out.writeBoolean(true);
			renderOrder.save(out);
		} else
			out.writeBoolean(false);
		// TODO spacePartitioning
	}
}
