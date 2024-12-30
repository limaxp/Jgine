package org.jgine.utils.loader;

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

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.UpdateOrder;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.spacePartitioning.SpacePartitioning;
import org.jgine.utils.math.spacePartitioning.SpacePartitioningTypes;

/**
 * Helper class for loading {@link Scene} files.
 */
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

	public static Scene load(DataInput in) throws IOException {
		// TODO transform calculates Matrix 2 times!
		String name = in.readUTF();
		Scene scene = Engine.getInstance().createScene(name);

		SpacePartitioning spacePartitioning = SpacePartitioningTypes.get(in.readInt()).get();
		spacePartitioning.load(in);
		scene.setSpacePartitioning(spacePartitioning);

		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++) {
			SystemScene<?, ?> systemScene = scene.setSystem(EngineSystem.get(in.readInt()));
			systemScene.load(in);
		}

		int entitySize = in.readInt();
		for (int i = 0; i < entitySize; i++)
			new Entity(scene).load(in);

		boolean hasUpdateOrder = in.readBoolean();
		if (hasUpdateOrder) {
			UpdateOrder updateOrder = new UpdateOrder();
			updateOrder.load(in);
			scene.setUpdateOrder(updateOrder);
		}

		int renderOrderSize = in.readInt();
		if (renderOrderSize > 0) {
			List<EngineSystem<?, ?>> renderOrder = new IdentityArrayList<EngineSystem<?, ?>>();
			for (int i = 0; i < renderOrderSize; i++)
				renderOrder.add(EngineSystem.get(in.readInt()));
			scene.setRenderOrder(renderOrder);
		}
		return scene;
	}

	public static void write(Scene scene, DataOutput out) throws IOException {
		out.writeUTF(scene.name);

		SpacePartitioning spacePartitioning = scene.getSpacePartitioning();
		out.writeInt(spacePartitioning.getType().getId());
		spacePartitioning.save(out);

		Collection<SystemScene<?, ?>> systems = scene.getSystems();
		out.writeInt(systems.size());
		for (SystemScene<?, ?> systemScene : systems) {
			out.writeInt(systemScene.id);
			systemScene.save(out);
		}

		List<Entity> entities = scene.getTopEntities();
		out.writeInt(entities.size());
		for (Entity entity : entities)
			entity.save(out);

		UpdateOrder updateOrder = scene.getUpdateOrder();
		if (updateOrder != null) {
			out.writeBoolean(true);
			updateOrder.save(out);
		} else
			out.writeBoolean(false);

		List<EngineSystem<?, ?>> renderOrder = scene.getRenderOrder();
		if (renderOrder != null) {
			int renderOrderSize = renderOrder.size();
			out.writeInt(renderOrderSize);
			for (int i = 0; i < renderOrderSize; i++)
				out.writeInt(renderOrder.get(i).id);
		} else
			out.writeInt(0);
	}
}
