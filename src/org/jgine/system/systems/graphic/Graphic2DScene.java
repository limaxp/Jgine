package org.jgine.system.systems.graphic;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.TaskManager;
import org.jgine.render.FrustumCulling2D;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class Graphic2DScene extends TransformListSystemScene<Graphic2DSystem, Material> {

	private final Queue<Object> renderQueue = new ConcurrentLinkedQueue<Object>();

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
	public void update() {
		renderQueue.clear();
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		FrustumCulling2D frustumCulling = new FrustumCulling2D();
		frustumCulling.applyCamera(camera);
		TaskManager.execute(size, (index, size) -> update(frustumCulling, index, size));
	}

	private void update(FrustumCulling2D frustumCulling, int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			Material object = objects[index];
			// TODO use collider
//			if (frustumCulling.containsPoint(object.transform.getPosition()))
			renderQueue.addAll(Arrays.asList(transforms[index], object));
		}
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.TEXTURE_SHADER);
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer2D.setCamera(camera);

		Iterator<Object> iter = renderQueue.iterator();
		while (iter.hasNext())
			Renderer2D.renderQuad(((Transform) iter.next()).getMatrix(), (Material) iter.next());
	}
}
