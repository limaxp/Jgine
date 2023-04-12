package org.jgine.utils.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;

/**
 * The default {@link SpacePartitioning} for a {@link Scene}. This basically
 * acts as if the {@link Scene} does not have any {@link SpacePartitioning} and
 * is the default {@link SpacePartitioning} a {@link Scene} uses after creation.
 * Checks will search the ENTIRE {@link Scene}.
 */
public class SceneSpacePartitioning implements SpacePartitioning {

	private Scene scene;

	public SceneSpacePartitioning() {
	}

	public SceneSpacePartitioning(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void add(Entity object) {
	}

	@Override
	public void remove(Entity object) {
	}

	@Override
	public void move(Entity object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
	}

	@Override
	public void forNear(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Entity> func) {
		getNear(xMin, yMin, zMin, xMax, yMax, zMax).forEach(func);
	}

	@Override
	public Collection<Entity> getNear(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		return scene.getEntities();
	}

	@Override
	public Entity get(double x, double y, double z, Entity opt_default) {
		for (Entity entity : scene.getEntities())
			if (entity.transform.getX() == x && entity.transform.getY() == y && entity.transform.getZ() == z)
				return entity;
		return opt_default;
	}

	@Override
	public void clear() {
	}

	@Override
	public SpacePartitioningType<?> getType() {
		return SpacePartitioningTypes.SCENE;
	}

	@Override
	public void load(DataInput in) throws IOException {
		scene = Engine.getInstance().getScene(in.readUTF());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(scene.name);
	}
}
