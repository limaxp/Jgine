package jgine.utils.loader;

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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.core.Entity;
import jgine.core.Scene;
import jgine.core.UpdateOrder;
import jgine.system.SystemScene;
import jgine.utils.Logger;
import jgine.utils.registry.Registry;

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
		Scene scene = new Scene(in.readUTF());
		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++) {
			SystemScene<?, ?> systemScene = scene.setSystem(Registry.SYSTEM.get(in.readInt()));
			systemScene.load(in);
		}

		int entitySize = in.readInt();
		for (int i = 0; i < entitySize; i++) {
			Entity entity = new Entity(scene);
			entity.load(in);
			entity.loadMap(in);
		}

		UpdateOrder updateOrder = new UpdateOrder();
		updateOrder.load(in);
		scene.setUpdateOrder(updateOrder);

		int renderOrderSize = in.readInt();
		IntList renderOrder = new IntArrayList(renderOrderSize);
		for (int i = 0; i < renderOrderSize; i++)
			renderOrder.add(in.readInt());
		scene.setRenderOrder(renderOrder);
		return scene;
	}

	public static void write(Scene scene, DataOutput out) throws IOException {
		out.writeUTF(scene.name);
		Collection<SystemScene<?, ?>> systems = scene.getSystems();
		out.writeInt(systems.size());
		for (SystemScene<?, ?> systemScene : systems) {
			out.writeInt(systemScene.id);
			systemScene.save(out);
		}

		List<Entity> entities = scene.getEntities();
		out.writeInt(entities.size());
		for (Entity entity : entities) {
			entity.save(out);
			entity.saveMap(out);
		}

		scene.getUpdateOrder().save(out);

		IntList renderOrder = scene.getRenderOrder();
		int renderOrderSize = renderOrder.size();
		out.writeInt(renderOrderSize);
		for (int i = 0; i < renderOrderSize; i++)
			out.writeInt(renderOrder.getInt(i));
	}
}
