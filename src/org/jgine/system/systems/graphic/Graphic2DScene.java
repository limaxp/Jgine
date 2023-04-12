package org.jgine.system.systems.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.FrustumCulling2D;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.system.SystemObject;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.utils.scheduler.TaskHelper;

public class Graphic2DScene extends TransformListSystemScene<Graphic2DSystem, Material> {

	private final Queue<Object> renderQueue = new ConcurrentLinkedQueue<Object>();
	private final FrustumCulling2D frustumCulling = new FrustumCulling2D();

	public Graphic2DScene(Graphic2DSystem system, Scene scene) {
		super(system, scene, Material.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Material object) {
	}

	@Override
	public void update(float dt) {
		renderQueue.clear();
		Camera camera = Engine.CAMERA_SYSTEM.getMainCamera();
		frustumCulling.applyCamera(camera, 50);
		TaskHelper.execute(size, (index, size) -> update(frustumCulling, index, size));
	}

	private void update(FrustumCulling2D frustumCulling, int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			Material object = objects[index];
			if (frustumCulling.containsPoint(transforms[index].getPosition()))
				renderQueue.addAll(Arrays.asList(transforms[index], object));
		}
//		scene.getSpacePartitioning().forNear(frustumCulling.x1, frustumCulling.y1, 0.0f, frustumCulling.x2,
//				frustumCulling.y2, 0.0f, (entity) -> {
//					SystemObject[] materials = entity.getSystems(Engine.GRAPHIC_2D_SYSTEM);
//					for (SystemObject material : materials)
//						renderQueue.addAll(Arrays.asList(entity.transform, material));
//				});
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.PHONG_2D_SHADER);
		Iterator<Object> iter = renderQueue.iterator();
		while (iter.hasNext())
			Renderer2D.renderQuad(((Transform) iter.next()).getMatrix(), (Material) iter.next());
	}

	@Override
	public Material load(DataInput in) throws IOException {
		Material object = new Material();
		object.load(in);
		return object;
	}

	@Override
	public void save(Material object, DataOutput out) throws IOException {
		object.save(out);
	}
}
