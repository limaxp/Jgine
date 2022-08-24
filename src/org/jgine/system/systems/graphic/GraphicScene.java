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
import org.jgine.render.FrustumCulling;
import org.jgine.render.Renderer;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class GraphicScene extends TransformListSystemScene<GraphicSystem, GraphicObject> {

	private final Queue<Object> renderQueue = new ConcurrentLinkedQueue<Object>();

	public GraphicScene(GraphicSystem system, Scene scene) {
		super(system, scene, GraphicObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, GraphicObject object) {
	}

	@Override
	public void update() {
		renderQueue.clear();
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		FrustumCulling frustumCulling = new FrustumCulling();
		frustumCulling.applyPerspective(camera.getPerspective());
		frustumCulling.applyCamera(camera);
		TaskManager.execute(size, (index, size) -> update(frustumCulling, index, size));
	}

	private void update(FrustumCulling frustumCulling, int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			GraphicObject object = objects[index];
			// TODO use collider
			// if (frustumCulling.containsPoint(object.transform.getPosition()))
			renderQueue.addAll(Arrays.asList(transforms[index], object));
		}
	}

	@Override
	public void render() {
		Renderer.setShader(Renderer.PHONG_SHADER);
		Renderer.enableDepthTest();
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer.setCamera(camera);

		Iterator<Object> iter = renderQueue.iterator();
		while (iter.hasNext())
			Renderer.render(((Transform) iter.next()).getMatrix(), ((GraphicObject) iter.next()).model);

		Renderer.disableDepthTest();
	}
}
